package jigit.settings;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jigit.indexer.repository.RepoType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;

public final class JigitRepoTypeAdapterFactory implements TypeAdapterFactory {
    @NotNull
    @Override
    public <T> TypeAdapter<T> create(@NotNull final Gson gson, @NotNull final TypeToken<T> typeToken) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, typeToken);
        return new TypeAdapter<T>() {
            @Override
            public void write(@NotNull final JsonWriter out, @NotNull final T value) throws IOException {
                delegateAdapter.write(out, value);
            }

            @NotNull @Override
            public T read(@NotNull final JsonReader in) throws IOException {
                final T value = delegateAdapter.read(in);
                postProcess(value);
                return value;
            }
        };
    }

    private void postProcess(@NotNull Object obj) {
        if (!(obj instanceof JigitRepo)) {
            return;
        }
        try {
            final Field[] declaredFields = JigitRepo.class.getDeclaredFields();
            for (final Field field : declaredFields) {
                if (field.getType() == RepoType.class) {
                    field.setAccessible(true);
                    if (field.get(obj) == null) {
                        field.set(obj, RepoType.SingleRepository);
                    }
                }
            }
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
