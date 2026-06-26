package br.com.banco.util;

public final class CpfValidator {

    private CpfValidator() {}

    public static String validar(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF não pode ser nulo.");
        }

        String digitos = cpf.replaceAll("\\D", "");

        if (digitos.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos.");
        }

        if (digitos.chars().allMatch(c -> c == digitos.charAt(0))) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        int[] nums = digitos.chars().map(c -> c - '0').toArray();

        int digito1 = calcularDigito(nums, 10);
        int digito2 = calcularDigito(nums, 11);

        if (nums[9] != digito1 || nums[10] != digito2) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        return digitos;
    }

    private static int calcularDigito(int[] nums, int peso) {
        int soma = 0;
        for (int i = 0; i < peso - 1; i++) {
            soma += nums[i] * (peso - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    public static String formatar(String cpf) {
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return cpf;
        }
        return digitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
