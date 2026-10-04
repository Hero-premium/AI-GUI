package org.hero;


import org.hero.tools.ToolRegistry;
import org.hero.tools.ToolsInformation;

import java.util.List;

/**
 * a class meant to hold requests related records
 */
public final class Requests {

    /**
     * don't instantiate this
     */
    private Requests() {
        throw new AssertionError("no org.hero.Requests instances for you!");
    }

    /**
     * the message being sent, can be either by the AI or the user
     *
     * @param role       the name of the sender
     * @param content    the content of the message.
     * @param tool_calls if the AI requested tools this is where they show up
     */
    public record Message(String role, String content, List<ToolsInformation.ToolCall> tool_calls, String tool_name) {
        public Message(String role, String content, String tool_name) {
            this(role, content, null, tool_name);
        }

        public Message(String role, String content) {
            this(role, content, null, null);
        }
    }

    /**
     * the request sent by the user or tools.
     *
     * @param model    the name of the model that is meant to receive this request
     * @param messages all the messages that has been sent this conversation, you handle adding/removing messages yourself
     * @param stream   whether the model will dump all the tokens at once or send one token at the time, for now we only support false
     */
    public record RequestIn(String model, List<Message> messages, boolean stream,
                            List<ToolsInformation.ToolData> tools) {
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
     */
    public record RequestOut(
            String model,
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
            long eval_duration
    ) {
        public int getEvalDurationInSeconds() {
            return (int) (eval_duration / 1_000_000_000L);
        }
    }

}
