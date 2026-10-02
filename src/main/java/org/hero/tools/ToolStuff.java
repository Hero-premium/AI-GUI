package org.hero.tools;

import java.util.*;

/**
 * contains tools-related records
 */
public class ToolStuff {

    public record ToolData(String type, Function function) {
        public ToolData(Function function) {
            this("function", function);
        }
    }

    /**
     *
     * @param type       the type of parameters the AI will use (string, int, boolean, etc.)
     * @param properties
     * @param required
     */
    public record Parameters(String type, Map<String, Property> properties, List<String> required) {
        public Parameters(Map<String, Property> properties, List<String> required) {
            this("object", properties, required);
        }
    }

    public record Function(String name, String description, Parameters parameters) {
    }

    public record Property(PropertiesType type, String description) {
    }

    public static class ToolDataBuilder {
        private Function function;
        private Parameters parameters;
        private final Map<String, Property> properties = new HashMap<>();
        private final List<String> required = new ArrayList<>();


        /**
         * use {@link startToolDataBuilder}
         */
        private ToolDataBuilder() {
        }


        public static ToolDataBuilder startToolDataBuilder() {
            return new ToolDataBuilder();
        }

        public ToolDataBuilder function(String name, String description) {
            if (this.parameters == null) throw new IllegalArgumentException("parameters has not been built yet");
            this.function = new Function(
                    Objects.requireNonNull(name, "name must not be null"),
                    Objects.requireNonNull(description, "description must not be null"),
                    Objects.requireNonNull(parameters, "parameters must not be null")
            );
            return this;
        }

        public ToolDataBuilder parameter(String argumentName, PropertiesType type, String description, boolean isRequired) {
            properties.put(Objects.requireNonNull(argumentName, "argumentName must not be null"),
                    new Property(
                            Objects.requireNonNull(type, "type must not be null"),
                            Objects.requireNonNull(description, "description must not be null")
                    ));
            if (isRequired) {
                required.add(argumentName);
            }
            return this;
        }


        public ToolDataBuilder buildParameters() {
            if (properties.isEmpty()) throw new IllegalStateException("no parameters has been passed before this call");
            parameters = new Parameters(properties, required);
            return this;
        }

        public ToolData build() {
            if (this.function == null) throw new IllegalStateException("function has not been built yet");
            return new ToolData(function);
        }
    }

    public enum PropertiesType {
        ARRAY, INTEGER, NUMBER, OBJECT, STRING, BOOLEAN, NULL,
    }

    /**
     * don't instantiate this
     */
    private ToolStuff() {
        throw new AssertionError("no org.hero.chatai.Requests.Tools instances for you!");
    }

}
