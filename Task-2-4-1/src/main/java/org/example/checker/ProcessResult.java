package org.example.checker;

import lombok.Value;

/**
 * Результат выполнения внешнего процесса.
 * @Value — иммутабельный класс: все поля final, только геттеры, нет сеттеров.
 */
@Value
public class ProcessResult {
    int     exitCode;
    String  output;
    boolean timedOut;

    /** true если процесс завершился с exit code 0 и не упал по таймауту. */
    public boolean isSuccess() {
        return !timedOut && exitCode == 0;
    }
}
