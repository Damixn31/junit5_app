package org.olmedo.junitapp.ejemplo.models;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.olmedo.junitapp.ejemplo.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // con esto podemos sacar el static de los metodos beforeAll y afterAll

class CuentaTest {

    Cuenta cuenta;

    @BeforeEach // metodo de ciclo de vida antes
    void initMetodoTest() {
        this.cuenta = new Cuenta("Damian", new BigDecimal("1000.12345"));
        System.out.println("Inciando el metodo.");
    }
    @AfterEach  //metodo de ciclo de vida
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba!");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Incianilizando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName("Probando nombre de la cuenta corriente!") // para que sea mas descriptivo
    void testNombreCuenta() {

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
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testeando referencias que sean iguales con el metodo equals.")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

//        assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
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
   // @Disabled // para que no falle
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll.")
    void testRelacionBancoCuentas() {
        //fail(); // Para forzar el error
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

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWidows() {

    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac() {

    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows(){

    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void soloJdk8(){

    }

    @Test
    @EnabledOnJre(JRE.JAVA_19)
    void soloJdk19(){

    }

    @Test
    @DisabledOnJre(JRE.JAVA_19)
    void testNoJdk19(){

    }

    @Test
    void imprimirSytemPropities(){
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = ".*19.*")
    void testJavaVersion(){

    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testSolo64(){}

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testNoSolo64(){}

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "hiro")
    void testUsername(){}

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void testDev(){}

    @Test
    void imprimirVariablesAmbiente(){
        Map<String, String> getenv = System.getenv();
        getenv.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-19.0.1.*")
    void javaHome(){}

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
    void testProcesadores(){}

    @Test
    @EnabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "dev")
    void testEnv(){}

    @Test
    @DisabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "prod")
    void testEnvProdDisabled(){}

    @Test
    @DisplayName("test Saldo CuentaDev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test Saldo CuentaDev2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

}