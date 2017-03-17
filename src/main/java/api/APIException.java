package api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class APIException extends IOException {
  
  private final int responseCode;

  public APIException(@Nullable String message, int responseCode, @NotNull Throwable cause) {
    super(message, cause);
    this.responseCode = responseCode;
  }

  public int getResponseCode() {
    return responseCode;
  }
}
