package io.github.hero_premium.util;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.tools.ToolsInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestOutBuilder {

    private final StringBuilder stringBuilder = new StringBuilder();
    private final List<ToolsInformation.ToolCall> toolCalls = new ArrayList<>();

    /**
     * keeps collecting the request out contents, and only returns the whole thing when the request is finished,
     * @param request the part of request to append
     * @return the full request, or {@code null} if the request is not finished
     */
    public @Nullable Requests.RequestOut appendMessages(@NotNull RequestOut request) {
        Message message = Objects.requireNonNull(request, "request must not be null").message();

        stringBuilder.append(message.content() != null ? message.content() : "");
        List<ToolsInformation.ToolCall> toolCalls = message.tool_calls();

        if (toolCalls != null && !toolCalls.isEmpty()) {
            this.toolCalls.addAll(toolCalls);
        }

        if (request.done()) {
            RequestOut requestOut = buildRequestOut(request);
            stringBuilder.setLength(0);
            this.toolCalls.clear();
            return requestOut;
        }
        return null;
    }

    private @NotNull RequestOut buildRequestOut(@NotNull RequestOut request) {
        Message message = request.message();

        return new RequestOut(
                request.model(),
                request.created_at(),
                new Message(message.role(), stringBuilder.toString(), toolCalls.isEmpty() ? null : List.copyOf(toolCalls)),
                true,
                request.done_reason(),
                request.total_duration(),
                request.load_duration(),
                request.prompt_eval_count(),
                request.prompt_eval_cached_count(),
                request.prompt_eval_duration(),
                request.eval_count(),
                request.eval_duration(),
                request.error());
    }
}
