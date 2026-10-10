# AI-GUI

A JavaFX chat GUI that coordinates local AI models (such as Ollama's) with AI tools, and lets you add your own commands.

## Requirements

- JDK 26 or newer
- [Ollama](https://ollama.com) running locally (default `http://localhost:11434`)
- The default model: `ollama pull llama3.2:3b`

## Quick start

```
git clone https://github.com/Hero-premium/ai-gui
cd ai-gui
./gradlew run
```

Make sure the Ollama server is running before you start.

## Extending

**Use a different AI or server.** Add a class in `org.hero.chatai` that extends `LocalAi` and implements `chat` with the right server URL. Then select it in `Program.java` (currently `new Llama3b()`).

**Add a tool.** Create a class in `org.hero.tools` that extends `Tool`, then add its fully qualified name to
`src/main/resources/META-INF/services/org.hero.tools.Tool`. The AI can then see and call it.

**Add a command.** Same steps, but in `org.hero.commands`, extending `Command`, and registered in
`src/main/resources/META-INF/services/org.hero.commands.Command`.

Commands are more powerful than tools: they get access to the current GUI and the AI.

## Roadmap

More tools and commands are coming.