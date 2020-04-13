package it.polimi.ingsw;

public interface Notifiable<T> {
    void notify(T value);
}
