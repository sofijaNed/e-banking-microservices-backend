package fon.bank.authservice.registration;

public final class Jmbg {
    private Jmbg() {}
    public static boolean isValid(String jmbg) {
        if (jmbg == null || jmbg.length() != 13) return false;
        for (int i = 0; i < 13; i++) if (!Character.isDigit(jmbg.charAt(i))) return false;
        int[] a = jmbg.chars().map(c -> c - '0').toArray();
        int k = 11 - ((7*(a[0]+a[6]) + 6*(a[1]+a[7]) + 5*(a[2]+a[8]) +
                4*(a[3]+a[9]) + 3*(a[4]+a[10]) + 2*(a[5]+a[11])) % 11);
        int c = (k > 9) ? 0 : k;
        return c == a[12];
    }
}
