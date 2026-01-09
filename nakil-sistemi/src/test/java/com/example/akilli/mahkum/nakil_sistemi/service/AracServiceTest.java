package com.example.akilli.mahkum.nakil_sistemi.service;


import com.example.akilli.mahkum.nakil_sistemi.model.Arac;
import com.example.akilli.mahkum.nakil_sistemi.repository.AracRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AracServiceTest {

    @Mock
    private AracRepository aracRepository;

    @InjectMocks
    private AracService aracService;

    private Arac arac1;
    private Arac arac2;

    @BeforeEach
    void setUp() {
        arac1 = new Arac();
        arac1.setId(1L);
        arac1.setPlaka("34ABC123");
        arac1.setModel("Mercedes Sprinter");
        arac1.setKapasite(10);
        arac1.setTip(Arac.AracTipi.NAKİL);
        arac1.setAktif(true);
        arac1.setBakimda(false);
        arac1.setServiste(false);

        arac2 = new Arac();
        arac2.setId(2L);
        arac2.setPlaka("34XYZ789");
        arac2.setModel("Ford Transit");
        arac2.setKapasite(8);
        arac2.setTip(Arac.AracTipi.ACİL);
        arac2.setAktif(true);
        arac2.setBakimda(false);
        arac2.setServiste(false);
    }

    @Test
    void tumAraclar_ShouldReturnAllAraclar() {
        // Arrange
        when(aracRepository.findAllByOrderByPlakaAsc()).thenReturn(Arrays.asList(arac1, arac2));

        // Act
        List<Arac> result = aracService.tumAraclar();

        // Assert
        assertEquals(2, result.size());
        verify(aracRepository, times(1)).findAllByOrderByPlakaAsc();
    }

    @Test
    void aracBul_WithValidPlaka_ShouldReturnArac() {
        // Arrange
        when(aracRepository.findByPlaka("34ABC123")).thenReturn(Optional.of(arac1));

        // Act
        Optional<Arac> result = aracService.aracBul("34ABC123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("34ABC123", result.get().getPlaka());
        verify(aracRepository, times(1)).findByPlaka("34ABC123");
    }

    @Test
    void aracKaydet_NewArac_ShouldSaveSuccessfully() {
        // Arrange
        Arac newArac = new Arac();
        newArac.setPlaka("06DEF456");
        newArac.setModel("Volkswagen Crafter");
        newArac.setKapasite(12);

        when(aracRepository.findByPlaka("06DEF456")).thenReturn(Optional.empty());
        when(aracRepository.save(any(Arac.class))).thenReturn(newArac);

        // Act
        Arac result = aracService.aracKaydet(newArac);

        // Assert
        assertNotNull(result);
        assertEquals("06DEF456", result.getPlaka());
        verify(aracRepository, times(1)).findByPlaka("06DEF456");
        verify(aracRepository, times(1)).save(newArac);
    }

    @Test
    void aracKaydet_DuplicatePlaka_ShouldThrowException() {
        // Arrange
        Arac newArac = new Arac();
        newArac.setPlaka("34ABC123");
        newArac.setModel("Yeni Model");

        when(aracRepository.findByPlaka("34ABC123")).thenReturn(Optional.of(arac1));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aracService.aracKaydet(newArac);
        });

        assertTrue(exception.getMessage().contains("Bu plaka ile kayıtlı araç zaten var"));
        verify(aracRepository, times(1)).findByPlaka("34ABC123");
        verify(aracRepository, never()).save(any());
    }

    @Test
    void aracKaydet_InvalidPlakaFormat_ShouldThrowException() {
        // Arrange
        Arac newArac = new Arac();
        newArac.setPlaka("INVALID123"); // Geçersiz format

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aracService.aracKaydet(newArac);
        });

        assertTrue(exception.getMessage().contains("Geçersiz plaka formatı"));
    }

    @Test
    void aracSil_ValidId_ShouldDeleteSuccessfully() {
        // Arrange
        when(aracRepository.existsById(1L)).thenReturn(true);
        when(aracRepository.findById(1L)).thenReturn(Optional.of(arac1));

        // Act
        boolean result = aracService.aracSil(1L);

        // Assert
        assertTrue(result);
        verify(aracRepository, times(1)).existsById(1L);
        verify(aracRepository, times(1)).deleteById(1L);
    }


    @Test
    void aktifAraclar_ShouldReturnOnlyActiveAraclar() {
        // Arrange
        when(aracRepository.findByAktifTrueOrderByPlakaAsc()).thenReturn(Arrays.asList(arac1, arac2));

        // Act
        List<Arac> result = aracService.aktifAraclar();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Arac::isAktif));
        verify(aracRepository, times(1)).findByAktifTrueOrderByPlakaAsc();
    }

    @Test
    void müsaitAraclar_ShouldReturnAvailableAraclar() {
        // Arrange
        when(aracRepository.findByAktifTrueAndBakimdaFalseAndServisteFalseOrderByPlakaAsc())
                .thenReturn(Arrays.asList(arac1, arac2));

        // Act
        List<Arac> result = aracService.musaitAraclar();

        // Assert
        assertEquals(2, result.size());
        verify(aracRepository, times(1))
                .findByAktifTrueAndBakimdaFalseAndServisteFalseOrderByPlakaAsc();
    }

    @Test
    void tipeGoreAraclar_ShouldReturnFilteredAraclar() {
        // Arrange
        when(aracRepository.findByTipOrderByPlakaAsc(Arac.AracTipi.NAKİL))
                .thenReturn(Arrays.asList(arac1));

        // Act
        List<Arac> result = aracService.tipeGoreAraclar(Arac.AracTipi.NAKİL);

        // Assert
        assertEquals(1, result.size());
        assertEquals(Arac.AracTipi.NAKİL, result.get(0).getTip());
        verify(aracRepository, times(1)).findByTipOrderByPlakaAsc(Arac.AracTipi.NAKİL);
    }

    @Test
    void aracDurumGuncelle_ShouldUpdateAktifStatus() {
        // Arrange
        when(aracRepository.findById(1L)).thenReturn(Optional.of(arac1));
        when(aracRepository.save(any(Arac.class))).thenReturn(arac1);

        // Act
        Arac result = aracService.aracDurumGuncelle(1L, false);

        // Assert
        assertNotNull(result);
        assertFalse(arac1.isAktif()); // Durum değişti
        verify(aracRepository, times(1)).findById(1L);
        verify(aracRepository, times(1)).save(arac1);
    }

    @Test
    void aracBakimaAl_ShouldUpdateBakimdaStatus() {
        // Arrange
        when(aracRepository.findById(1L)).thenReturn(Optional.of(arac1));
        when(aracRepository.save(any(Arac.class))).thenReturn(arac1);

        // Act
        Arac result = aracService.aracBakimaAl(1L, true);

        // Assert
        assertNotNull(result);
        assertTrue(arac1.isBakimda());
        verify(aracRepository, times(1)).findById(1L);
        verify(aracRepository, times(1)).save(arac1);
    }

    @Test
    void plakaVarMi_ShouldReturnTrueIfExists() {
        // Arrange
        when(aracRepository.existsByPlaka("34ABC123")).thenReturn(true);

        // Act
        boolean result = aracService.plakaVarMi("34ABC123");

        // Assert
        assertTrue(result);
        verify(aracRepository, times(1)).existsByPlaka("34ABC123");
    }

    @Test
    void aramaYap_ShouldReturnMatchingAraclar() {
        // Arrange
        String keyword = "Mercedes";
        when(aracRepository.findByPlakaContainingOrModelContainingOrMarkaContainingAllIgnoreCase(
                keyword, keyword, keyword)).thenReturn(Arrays.asList(arac1));

        // Act
        List<Arac> result = aracService.aramaYap(keyword);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getModel().contains(keyword));
        verify(aracRepository, times(1))
                .findByPlakaContainingOrModelContainingOrMarkaContainingAllIgnoreCase(keyword, keyword, keyword);
    }

    @Test
    void toplamAracSayisi_ShouldReturnCount() {
        // Arrange
        when(aracRepository.count()).thenReturn(5L);

        // Act
        long result = aracService.toplamAracSayisi();

        // Assert
        assertEquals(5L, result);
        verify(aracRepository, times(1)).count();
    }
}
