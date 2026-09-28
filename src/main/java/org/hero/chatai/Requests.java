package org.hero.chatai;

import java.util.List;

public class Requests {


    public record Message(String role, String content) {
    }

    public record RequestIn(String model, List<Message> messages, boolean stream) {
    }

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

    private Requests(){
        throw new AssertionError("no org.hero.chatai.Requests instances for you!");
    }
}
