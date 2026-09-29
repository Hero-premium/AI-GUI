package org.hero.chatai;

import java.util.List;

/**
 * a class meant to hold requests related records
 */
public class Requests {

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
     *  what the model returns
     * @param model the model name
     * @param created_at
     * @param message the message containing the modes role (typically assistant) and the content of the message
     * @param done
     * @param done_reason
     * @param total_duration
     * @param load_duration
     * @param prompt_eval_count
     * @param prompt_eval_cached_count
     * @param prompt_eval_duration
     * @param eval_count
     * @param eval_duration
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
    }

    /**
     * don't instantiate this
     */
    private Requests() {
        throw new AssertionError("no org.hero.chatai.Requests instances for you!");
    }
}
