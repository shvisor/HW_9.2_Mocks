package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.doAnswer;

public class MedicalServiceImplTest {
    PatientInfo patientInfo = new PatientInfo("2374b10e-fd4a-47a3-82f1-cc6aee558a62", "Иван", "Петров", LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));
    String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());


    @Test
    @DisplayName("Вывод сообщения во время проверки давления")
    public void shouldCheckBloodPressure() {
        PatientInfoFileRepository repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById("2374b10e-fd4a-47a3-82f1-cc6aee558a62"))
                .thenReturn(patientInfo);

        SendAlertService alert = Mockito.mock(SendAlertService.class);
        doAnswer(i -> null).when(alert).send(message);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(repository, alert);
        medicalService.checkBloodPressure("2374b10e-fd4a-47a3-82f1-cc6aee558a62", new BloodPressure(160, 120));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alert).send(argumentCaptor.capture());
        Mockito.verify(alert, Mockito.atLeastOnce()).send(message);
        Assertions.assertEquals(message, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Вывод сообщения во время проверки температуры")
    public void shouldCheckTemperature() {
        PatientInfoFileRepository repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById("2374b10e-fd4a-47a3-82f1-cc6aee558a62"))
                .thenReturn(patientInfo);

        SendAlertService alert = Mockito.mock(SendAlertService.class);
        doAnswer(i -> null).when(alert).send(message);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(repository, alert);
        medicalService.checkTemperature("2374b10e-fd4a-47a3-82f1-cc6aee558a62", new BigDecimal("35.14"));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alert).send(argumentCaptor.capture());
        Mockito.verify(alert, Mockito.atLeastOnce()).send(message);
        Assertions.assertEquals(message, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Отсутствие сообщения при нормальных показателях")
    public void shouldMessageIfNorm() {
        PatientInfoFileRepository repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById("2374b10e-fd4a-47a3-82f1-cc6aee558a62"))
                .thenReturn(patientInfo);

        SendAlertService alert = Mockito.mock(SendAlertService.class);
        doAnswer(i -> null).when(alert).send(message);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(repository, alert);
        medicalService.checkBloodPressure("2374b10e-fd4a-47a3-82f1-cc6aee558a62", new BloodPressure(120, 80));
        medicalService.checkTemperature("2374b10e-fd4a-47a3-82f1-cc6aee558a62", new BigDecimal("36.65"));
        Mockito.verify(alert, Mockito.never()).send(message);
    }

    @Test
    @DisplayName("Пациент с таким id отсутствует")
    public void shouldNoPatient() {
        PatientInfoFileRepository repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById("2374b10e-fd4a-47a3-82f1-cc6aee558a62"))
                .thenReturn(null);

        SendAlertService alert = Mockito.mock(SendAlertService.class);
        doAnswer(i -> null).when(alert).send(message);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(repository, alert);
        Assertions.assertThrows(RuntimeException.class, () -> medicalService.checkBloodPressure("2374b10e-fd4a-47a3-82f1-cc6aee558a62", new BloodPressure(160, 120)));
        Assertions.assertThrows(RuntimeException.class, () -> medicalService.checkTemperature("2374b10e-fd4a-47a3-82f1-cc6aee558a62", new BigDecimal("36.65")));
    }
}
