package org.olmedo.junitapp.ejemplo.models;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.olmedo.junitapp.ejemplo.exception.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {
    @Test
    @DisplayName("Probando nombre de la cuenta corriente!") // para que sea mas descriptivo
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Damian", new BigDecimal("1000.12345"));
//        cuenta.setPersona("Damian");
        String esperado = "Damian";
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, real,  () -> "El nombre de la cuenta no es el que se esperaba: se esperaba " + esperado
                + " sin embargo fue " + real);
        assertTrue(real.equals("Damian"),  () -> "Nombre cuenta esperado debe ser igual a la real");
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperando.")
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Damian", new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testeando referencias que sean iguales con el metodo equals.")
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

//        assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Damian", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Damian", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Damian", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    @Disabled // para que no falle
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll.")
    void testRelacionBancoCuentas() {
        fail(); // Para forzar el error
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertAll(() -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                                    () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(),
                                  () -> "El banco no tienes las cuentas esperadas"),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst()
                            .get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Jhon Doe"))));
    }

}