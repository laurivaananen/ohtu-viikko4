package ohtu.verkkokauppa;

import org.junit.*;

import ohtu.verkkokauppa.Kauppa;
import ohtu.verkkokauppa.Pankki;
import ohtu.verkkokauppa.Varasto;
import ohtu.verkkokauppa.Viitegeneraattori;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.beans.Transient;

public class KauppaTest {

    Kauppa kauppa;

    @Test
    public void ostoksenPaatyttyaPankinMethodiaTilisiirtoKutsutaan() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(5));
    }

    @Test
    public void monenOstoksenPaatyttyaKutsutaanTilisiirto() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(11));
    }

    @Test
    public void monenSamanOstoksenPaatyttyaKutsutaanTilisiirto() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(10));
    }

    @Test
    public void tuotettaEiLisataJosSitaEiOle() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(5));
    }

    @Test
    public void aloitaAsiointiNollaaEdellistenOstostenTiedot() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(5));
    }

    @Test
    public void uusiViitenumeroJokaiselleOstokselle() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        verify(viite, times(1)).uusi();

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");

        verify(viite, times(2)).uusi();
    }
    
    @Test
    public void poistaKoristaSiirtaaTuotteenVarastoon() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);

        Tuote tuote = new Tuote(1, "maito", 5);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(tuote);

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.poistaKorista(1);
        verify(varasto).palautaVarastoon(tuote);

    }


}