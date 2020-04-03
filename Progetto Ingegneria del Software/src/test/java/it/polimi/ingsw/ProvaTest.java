package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ProvaTest {

    @Test
    void test(){
        Prova somma = new Prova();
        assertEquals(2, somma.add(1, 1));
    }
}
