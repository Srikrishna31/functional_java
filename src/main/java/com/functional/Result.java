package src.main.java.com.functional;

/**
 * This class handles the results of a computation, and allows to bind further
 * computations on those resutls.
 * It provides two subclasses: Success and Failure, which encapsulate the corresponding
 * cases.
 * The way to instantiate them is through static methods provided and not directly.
 */
public interface Result<T> {
    /**
     * This method handles the effects to be applied to the Result object.
     */
    void bind(Effect<T> success, Effect<String> failure);

    static <T> Result<T> failure(String message) {
        return new Failure<>(message);
    }

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    class Success<T> implements Result<T> {
        private final T value;

        private Success(T t) {
            value = t;
        }

        /**
         * Success implements bind, by applying the success effect to the value.
         * @param success : function to apply on successful computation.
         * @param failure : function to apply on failure, which is ignored here.
         */
        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            success.apply(value);
        }
    }

    class Failure<T> implements Result<T> {
        private final String errorMessage;

        private Failure(String e) {
            errorMessage = e;
        }

        /**
         * Failure implements bind, by applying the failure effect to the error message.
         * @param success : function to apply on successful computation, which is ignored here.
         * @param failure : function to apply on failure.
         */
        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            failure.apply(errorMessage);
        }
    }
}
