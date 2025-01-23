package com.aloc.aloc.color.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.repository.ColorRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class ColorServiceTest {

  @Mock ColorRepository colorRepository;

  @InjectMocks ColorService colorService;

  private Color color;

  @BeforeEach
  void setUp() {
    color = Color.builder().id("Green").color1("#2ADC0D").category("common").build();
  }

  @Test
  @DisplayName("색상 조회 테스트")
  void getColorByIdTest() {
    // given
    String colorId = "Green";
    when(colorRepository.findById(colorId)).thenReturn(Optional.of(color));

    // when
    Color result = colorService.getColorById(colorId);

    // then
    assertNotNull(result);
    assertEquals(colorId, result.getId());
    assertEquals("#2ADC0D", result.getColor1());
    assertEquals("common", result.getCategory());

    verify(colorRepository).findById(colorId);
  }

  @Test
  @DisplayName("색상 조회 실패 테스트")
  void getColorByIdFailTest() {
    // given
    String colorId = "Red";
    when(colorRepository.findById(colorId)).thenReturn(Optional.empty());

    // when
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> colorService.getColorById(colorId));

    // then
    assertEquals("해당 컬러가 없습니다. " + colorId, exception.getMessage());
    verify(colorRepository).findById(colorId);
  }
}
