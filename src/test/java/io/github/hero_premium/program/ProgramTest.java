package io.github.hero_premium.program;

import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Roles;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.tools.ToolsInformation;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
// this file is AI generated, unfortunately.
class ProgramTest {

    // ---------------------------------------------------------------
    // ADJUST THESE: things I couldn't see
    // ---------------------------------------------------------------

    /** Number of AI calls expected when the AI requests a tool on every round. Pins current behavior. */
    private static final int CAP_ROUNDS = 5;

    /** Role used for assistant messages. ASSUMED name. */
    private static final Roles ASSISTANT = Roles.ASSISTANT;

    /** Role used for tool result messages. ASSUMED name. */
    private static final Roles TOOL = Roles.TOOL;


    private static ToolsInformation.ToolCall toolCall(String name) {
        return new ToolsInformation.ToolCall("", new ToolsInformation.FunctionCall(1, name, Map.of()));
    }

    /** Name of a tool that really exists in ToolRegistry (for the happy-path tool test). */
    private static final String KNOWN_TOOL = "FILL_ME_IN";

    // ---------------------------------------------------------------
    // fakes
    // ---------------------------------------------------------------

    /** Records every call; replies come from a script (one entry per AI call, each entry = chunks to emit). */
    static final class FakeAi extends LocalAi {
        final List<List<Message>> received = new CopyOnWriteArrayList<>();
        final BlockingQueue<List<RequestOut>> script = new LinkedBlockingQueue<>();
        volatile CountDownLatch gate; // if set, chat() waits on it before replying
        volatile CountDownLatch entered; // counted down when chat() is entered

        public FakeAi() {
            super("my name is unknown");
        }

        @Override
        public void chat(List<Message> messages, Consumer<RequestOut> callback) {
            received.add(List.copyOf(messages)); // snapshot, Program may mutate its list later
            if (entered != null) entered.countDown();
            if (gate != null) {
                try {
                    assertTrue(gate.await(5, TimeUnit.SECONDS), "gate never opened");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            List<RequestOut> chunks = script.poll();
            if (chunks == null) chunks = reply("(script exhausted)");
            chunks.forEach(callback);
        }

        FakeAi thenReply(String text) {
            script.add(reply(text));
            return this;
        }

        FakeAi thenToolCall(String toolName) {
            script.add(List.of(out(new Message(ASSISTANT, "", List.of(toolCall(toolName))), true)));
            return this;
        }
    }

    static final class FakeClient extends Client {
        final List<String> events = new CopyOnWriteArrayList<>();

        @Override
        public void launchApplication() {
        }

        @Override
        public void displayUserMessage(@NonNull String m) {
            events.add("user:" + m);
        }

        @Override
        public void displayToolsMessage(@NonNull String m) {
            events.add("tool:" + m);
        }

        @Override
        public void displayAIMessage(@NonNull String m) {
            events.add("ai:" + m);
        }

        @Override
        public void displaySystemMessage(@NonNull String m) {
            events.add("system:" + m);
        }

        @Override
        public void endLine() {
            events.add("endLine");
        }

        long count(String prefix) {
            return events.stream().filter(e -> e.startsWith(prefix)).count();
        }

        boolean has(String prefix) {
            return count(prefix) > 0;
        }
    }

    private static RequestOut out(Message m, boolean done) {
        return new RequestOut("test-model", "now", m, done, done ? "stop" : null, 0L, 0L, 0, 0, 0L, 0, 0L);
    }

    private static List<RequestOut> reply(String text) {
        return List.of(out(new Message(ASSISTANT, text), true));
    }

    private final FakeClient client = new FakeClient();
    private final FakeAi ai = new FakeAi();

    private Program program() {
        return new Program(client, ai); // ASSUMED parameter order (client, AI)
    }

    // ---------------------------------------------------------------
    // plain turns
    // ---------------------------------------------------------------

    @Test
    void plainTurn_showsUserMessageThenAiReply() {
        ai.thenReply("hello there");
        program().chat("hi");

        assertTrue(client.events.contains("user:hi"));
        assertTrue(client.events.contains("ai:hello there"));
        assertTrue(client.events.indexOf("user:hi") < client.events.indexOf("ai:hello there"));
    }

    @Test
    void plainTurn_aiReceivesUserMessage() {
        ai.thenReply("ok");
        program().chat("what's up");

        assertEquals(1, ai.received.size());
        List<Message> sent = ai.received.getFirst();
        Message last = sent.getLast();
        assertEquals("what's up", last.content());
    }

    @Test
    void secondTurn_historyIncludesFirstTurn() {
        ai.thenReply("first answer").thenReply("second answer");
        Program p = program();
        p.chat("one");
        p.chat("two");

        List<Message> secondCall = ai.received.get(1);
        assertTrue(secondCall.stream().anyMatch(m -> "one".equals(m.content())));
        assertTrue(secondCall.stream().anyMatch(m -> "first answer".equals(m.content())));
        assertTrue(secondCall.stream().anyMatch(m -> "two".equals(m.content())));
    }

    @Test
    void streaming_multipleChunksAllReachClient() {
        ai.script.add(List.of(
                out(new Message(ASSISTANT, "Hel"), false),
                out(new Message(ASSISTANT, "lo"), true)));
        program().chat("hi");

        String shown = String.join("", client.events.stream()
                .filter(e -> e.startsWith("ai:")).map(e -> e.substring(3)).toList());
        assertEquals("Hello", shown);
        assertTrue(client.has("endLine"), "endLine should mark the end of a streamed message");
    }

    // ---------------------------------------------------------------
    // blank input
    // ---------------------------------------------------------------

    @Test
    void blankInput_doesNotThrow() {
        ai.thenReply("ok");
        assertDoesNotThrow(() -> program().chat(""));
    }

    @Test
    void blankInputDoesNotReachAi() {
        ai.thenReply("ok");
        program().chat("");
        assertEquals(0, ai.received.size());
    }

    // ---------------------------------------------------------------
    // tools
    // ---------------------------------------------------------------

    @Test
    void unknownTool_resultSentBackAsUnknownTool() {
        ai.thenToolCall("definitely_not_a_tool").thenReply("done");
        program().chat("do it");

        assertEquals(2, ai.received.size(), "tool results should trigger a second AI call");
        List<Message> second = ai.received.get(1);
        Message toolResult = second.stream().filter(m -> m.role() == TOOL).findFirst().orElseThrow();
        assertEquals("definitely_not_a_tool", toolResult.tool_name());
        assert toolResult.content() != null;
        assertTrue(toolResult.content().toLowerCase().contains("unknown tool"));
    }

    @Test
    void knownTool_resultSentBackAndShownOnClient() {
        ai.thenToolCall(KNOWN_TOOL).thenReply("done");
        program().chat("do it");

        assertEquals(2, ai.received.size());
        assertTrue(client.has("tool:"), "displayToolsMessage should be called for the tool");
        assertTrue(ai.received.get(1).stream().anyMatch(m -> m.role() == TOOL && KNOWN_TOOL.equals(m.tool_name())));
    }

    @Test
    void toolCalls_inOneResponse_areAllSentBackTogether() {
        ai.script.add(List.of(out(new Message(ASSISTANT, "",
                List.of(toolCall("unknown_a"), toolCall("unknown_b"))), true)));
        ai.thenReply("done");
        program().chat("do both");

        assertEquals(2, ai.received.size(), "one batch, one follow-up call");
        long toolResults = ai.received.get(1).stream().filter(m -> m.role() == TOOL).count();
        assertEquals(2, toolResults);
    }

    @Test
    void toolCalls_collectedAcrossChunksUntilDone() {
        ai.script.add(List.of(
                out(new Message(ASSISTANT, "", List.of(toolCall("unknown_a"))), false),
                out(new Message(ASSISTANT, "", List.of(toolCall("unknown_b"))), true)));
        ai.thenReply("done");
        program().chat("go");

        assertEquals(2, ai.received.size(), "tools must not be sent back before done");
        assertEquals(2, ai.received.get(1).stream().filter(m -> m.role() == TOOL).count());
    }

    // ---------------------------------------------------------------
    // round cap
    // ---------------------------------------------------------------

    @Test
    void roundCap_stopsLoopAndShowsUserFacingMessage() {
        for (int i = 0; i < CAP_ROUNDS + 5; i++) ai.thenToolCall("unknown_loop");
        program().chat("loop forever");

        // characterization: pins the current off-by-one. Adjust CAP_ROUNDS if this fails.
        assertEquals(CAP_ROUNDS, ai.received.size());
        assertTrue(client.has("ai:") || client.has("system:"),
                "a message should be shown when the cap is hit");
    }

    @Test
    void roundCap_belowCapStillWorks() {
        for (int i = 0; i < CAP_ROUNDS - 1; i++) ai.thenToolCall("unknown_loop");
        ai.thenReply("finished");
        program().chat("go");

        assertTrue(client.events.contains("ai:finished"));
    }

    // ---------------------------------------------------------------
    // thread safety
    // ---------------------------------------------------------------

    @Test
    void concurrentChat_bothMessagesProcessed() throws Exception {
        ai.thenReply("a").thenReply("b");
        ai.entered = new CountDownLatch(1);
        ai.gate = new CountDownLatch(1);
        Program p = program();

        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            Future<?> first = pool.submit(() -> p.chat("first"));
            assertTrue(ai.entered.await(5, TimeUnit.SECONDS));

            // first turn is now stuck inside the AI
            Future<?> second = pool.submit(() -> p.chat("second"));
            Thread.sleep(200); // let the second call hit whatever lock/queue exists

            ai.gate.countDown();
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
        }

        // characterization: assert whichever behavior the code has. At minimum both get processed in order.
        assertEquals(2, ai.received.size());
        assertTrue(ai.received.get(0).stream().anyMatch(m -> "first".equals(m.content())));
        assertTrue(ai.received.get(1).stream().anyMatch(m -> "second".equals(m.content())));
    }

    @Test
    void toolCalls_doNotReExecuteInNextTurn() {
        ai.thenToolCall("unknown_a").thenReply("done").thenReply("second answer");
        Program p = program();

        p.chat("first");
        long toolEventsAfterTurn1 = client.count("tool:");
        int aiCallsAfterTurn1 = ai.received.size();

        p.chat("second");

        assertEquals(aiCallsAfterTurn1 + 1, ai.received.size(),
                "turn 2 has no tool calls, so it should be exactly one AI call");
        assertEquals(toolEventsAfterTurn1, client.count("tool:"),
                "turn 1's tool calls must not run again in turn 2");
    }

    @Test
    void toolCalls_notDuplicatedAcrossRoundsWithinATurn() {
        ai.thenToolCall("unknown_a").thenToolCall("unknown_b").thenReply("done");
        program().chat("go");

        assertEquals(3, ai.received.size());
        List<Message> finalCall = ai.received.get(2);
        long resultsForA = finalCall.stream()
                .filter(m -> m.role() == TOOL && "unknown_a".equals(m.tool_name())).count();
        long resultsForB = finalCall.stream()
                .filter(m -> m.role() == TOOL && "unknown_b".equals(m.tool_name())).count();

        assertEquals(1, resultsForA, "round 1's result must not be re-sent as pending in round 2");
        assertEquals(1, resultsForB);
    }

    @Test
    void roundCap_resetsAfterHittingCapInPreviousTurn() {
        Program p = program();

        // turn 1: tool call every round, exactly CAP_ROUNDS replies, so the cap ends it
        for (int i = 0; i < CAP_ROUNDS; i++) ai.thenToolCall("unknown_loop");
        p.chat("loop");
        assertEquals(CAP_ROUNDS, ai.received.size());
        ai.script.clear(); // make sure nothing from turn 1 is left over

        // turn 2: CAP_ROUNDS - 1 tool rounds, then a normal answer. Fails if the counter carried over.
        for (int i = 0; i < CAP_ROUNDS - 1; i++) ai.thenToolCall("unknown_loop");
        ai.thenReply("finished");
        p.chat("again");

        assertEquals(CAP_ROUNDS * 2, ai.received.size());
        assertTrue(client.events.contains("ai:finished"),
                "turn 2 should get a full set of rounds");
    }

    @Test
    void roundCap_resetsAfterNormalTurnThatUsedRounds() {
        Program p = program();

        for (int turn = 1; turn <= 3; turn++) {
            ai.script.clear();
            for (int i = 0; i < CAP_ROUNDS - 1; i++) ai.thenToolCall("unknown_loop");
            ai.thenReply("finished " + turn);
            p.chat("turn " + turn);

            assertTrue(client.events.contains("ai:finished " + turn),
                    "turn " + turn + " should not be cut short by earlier turns' rounds");
        }
    }
}
