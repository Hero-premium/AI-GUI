package org.hero.chatai;

import java.util.List;

/**
 * a class meant to hold requests related records
 */
public class Requests {

    /**
     * don't instantiate this
     */
    private Requests() {
        throw new AssertionError("no org.hero.chatai.Requests instances for you!");
    }

    /**
     * the message being sent, can be either by the AI or the user
     *
     * @param role    the name of the sender
     * @param content the content of the message, this is what typically want to show the user
     */
    public record Message(String role, String content) {
    }

    /**
     * the request the user send you.
     *
     * @param model    the name of the model that is meant to receive this request
     * @param messages all the messages that has been sent this conversation, you handle adding/removing messages yourself
     * @param stream   whether the model will dump all the tokens at once or sent one token at the time, for now we only support false
     */
    public record RequestIn(String model, List<Message> messages, boolean stream) {
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
     * @param prompt_eval_cached_count number of prompt tokens served from cache instead of being re-evaluated
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
            return (int) eval_duration / 1_000_000_000;
        }
    }
}
