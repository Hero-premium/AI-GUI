[![Java CI](https://github.com/Hero-premium/AI-GUI/actions/workflows/build.yml/badge.svg)](https://github.com/Hero-premium/AI-GUI/actions/workflows/build.yml)

# AI-GUI

A layer between Ollama and the user that adds tools and commands and a client, modders are free to add any of those

## Requirements

- JDK 25
- [Ollama](https://ollama.com) running locally
- an ollama model with chatting capabilities (e.g. llama3.2:3b or qwen3:4b)

## Quick start

Make sure the Ollama server is running before you start.

## Extending

**Use a different AI or server.** this is not required since AI-GUI discovers available models automatically, but for a
custom implementation (e.g. inserting a custom system prompt) a modder may add a class in
`io.github.hero_premium.localais` that extends `LocalAi` and implements `chat`.

**Add a tool.** Create a class in `io.github.hero_premium.tools` that extends `Tool`, then add its fully qualified name
to
`src/main/resources/META-INF/services/io.github.hero_premium.tools.Tool`. The AI can then see and call it.

**Add a command.** Same steps, but in `io.github.hero_premium.commands`, extending `Command`, and registered in
`src/main/resources/META-INF/services/io.github.hero_premium.commands.Command`.

Commands are more powerful than tools: they get access to the current Client and the AI.

