package io.github.hero_premium;


import io.github.hero_premium.tools.ToolRegistry;
import io.github.hero_premium.tools.ToolsInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * a class meant to hold requests related records
 */
public final class Requests {

    private static final Logger LOGGER = LoggerFactory.getLogger(Requests.class);

    /**
     * don't instantiate this
     */
    private Requests() {
        throw new AssertionError("no io.github.hero_premium.Requests instances for you!");
    }

    /**
     * the message being sent, can be either by the AI or the user
     *
     * @param role       the name of the sender
     * @param content    the content of the message.
     * @param tool_calls if the AI requested tools this is where they show up
     * @param tool_name  the name of the tool that generated this response if it was tool generated.
     */
    public record Message(Roles role, String content, List<ToolsInformation.ToolCall> tool_calls, String tool_name) {

        public Message(Roles role, String content, String tool_name) {
            this(role, content, null, tool_name);
        }

        public Message(Roles role, String content) {
            this(role, content, null, null);
        }
    }

    /**
     * the request sent by the user or tools.
     *
     * @param model    the name of the model that is meant to receive this request
     * @param messages all the messages that has been sent this conversation, you handle adding/removing messages yourself
     * @param stream   whether the model will dump all the tokens at once or send one token at the time, for now we only support false
     * @param tools    every available tool
     */
    public record RequestIn(String model, List<Message> messages, boolean stream,
                            Set<ToolsInformation.ToolData> tools) {
        public RequestIn(String model, List<Message> message, boolean stream) {
            this(model, message, stream, ToolRegistry.TOOLS_DATA);
        }
    }

    /**
     * Response returned by the model for a chat request.
     *
     * @param model                    the name of the model that produced the response
     * @param created_at               timestamp of when the response was created (ISO 8601)
     * @param message                  the generated message, containing the model's role (typically "assistant") and the content of the message
     * @param done                     whether the model has finished generating; false for intermediate streamed chunks
     * @param done_reason              why generation ended (e.g. "stop" or "length"); only set when done is true
     * @param total_duration           total time spent handling the request, in nanoseconds
     * @param load_duration            time spent loading the model, in nanoseconds
     * @param prompt_eval_count        number of tokens in the prompt
     * @param prompt_eval_cached_count number of prompt tokens reported as served from Ollama's KV cache rather than re-evaluated;
     *                                 listed in the /api/chat reference but undocumented elsewhere and often absent from responses,
     *                                 so it defaults to 0 when missing. Don't rely on it for accurate cache metrics
     * @param prompt_eval_duration     time spent evaluating the prompt, in nanoseconds
     * @param eval_count               number of tokens generated in the response
     * @param eval_duration            time spent generating the response, in nanoseconds
     * @param error                    if something went wrong while generating this response otherwise it's null
     */
    public record RequestOut(
            String model,
            String created_at,
            @NotNull Message message,
            boolean done,
            String done_reason,
            long total_duration,
            long load_duration,
            int prompt_eval_count,
            int prompt_eval_cached_count,
            long prompt_eval_duration,
            int eval_count,
            long eval_duration,
            @Nullable String error
    ) {
        @SuppressWarnings("ConstantConditions")
        public RequestOut {
            if (error != null) {
                LOGGER.error(error);
            }
            if (message == null) {
                message = new Message(Roles.SYSTEM, "failed to generate a response");
            }
        }

        public RequestOut(String model,
                          String created_at,
                          Message message,
                          boolean done,
                          String done_reason,
                          long total_duration,
                          long load_duration,
                          int prompt_eval_count,
                          int prompt_eval_cached_count,
                          long prompt_eval_duration,
                          int eval_count,
                          long eval_duration) {
            this(model, created_at, message, done, done_reason, total_duration, load_duration,
                    prompt_eval_count, prompt_eval_cached_count, prompt_eval_duration, eval_count, eval_duration, null);
        }

        public int getEvalDurationInSeconds() {
            return (int) (eval_duration / 1_000_000_000L);
        }
    }

}
