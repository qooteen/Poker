import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    private static final String SHARED_FILE = "Share.txt";
    private static final String ALICES_PAIR_FILE = "AlicesPair.txt";
    private static final String BOBS_PAIR_FILE = "BobsPair.txt";
    private static final String CAROLS_PAIR_FILE = "CarolPair.txt";

    public static void main(String[] args) {

        System.out.println("Выберете команду:" +
                "\n0 - Сгенерировать общее p - БПЧ" +
                "\n1 - Сгенерировать пары ключей для Алисы, Боба и Кэрол" +
                "\n2 - Generate Alica's secrete parameter Sa" +
                "\n3 - Alice select random secrete integer r and computes x" +
                "\n4 - Alice send pair (Ia, x) to Bob" +
                "\n5 - Bob select and send e to Alice" +
                "\n6 - Alice computes and send to Bob y" +
                "\n7 - Bob calculate redundant identifier Ja and computes z" +
                "\n8 - Bob check that z = x and z != 0");

        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        start(a);
    }

    public static void start(int a) {

        switch (a) {
            case 0: {
                System.out.println("KDC generating self and share params");

                BigInteger p = KDC.generatePrime();

                BigInteger q = KDC.generatePrime();

                BigInteger n = KDC.calculateN(p, q);
                BigInteger fi = KDC.calculateFI(p, q);

                BigInteger v = KDC.generateV(fi);
                BigInteger s = KDC.generateS(v, fi);

                String sharedParams = "n:" + n + "\n" + "v:" + v;
                String kdcParams = "p:" + p + "\n" + "q:" + q + "\n" + "n:" + n + "\n" +
                        "s:" + s + "\n" + "v:" + v + "\n" + "fi:" + fi;
                writeInFile(SHARED_FILE, sharedParams);
                writeInFile(KDC_FILE, kdcParams);
                System.out.println("Shared params n, v are generated in Share.txt");
                System.out.println("KDC params p, q, s, n, v fi are generated in KDC.txt");
                break;
            }
            case 1: {
                System.out.println("Alice calculate redundant identifier Ja = f(Ia), " +
                        "where Ia - her unique identifier, f - a well-known hash function");
                String ia = readFromFile(ALICES_IDENTIFIER_FILE);
                String sharedParams = readFromFile(SHARED_FILE);
                BigInteger n = new BigInteger(sharedParams.split("n:")[1].split("v:")[0]);
                BigInteger ja = KDC.encode(ia, n);
                String alicesParams = "Ja:" + ja;
                writeInFile(ALICES_PARAMS_FILE, alicesParams);
                System.out.println("Alice's redundant identifier Ja were generated in AlicesParams.txt");
                break;
            }
            case 2: {
                System.out.println("KDC generating Alice's secret parameter Sa");
                String sharedParams = readFromFile(SHARED_FILE);
                String kdcParams = readFromFile(KDC_FILE);
                String alicesParams = readFromFile(ALICES_PARAMS_FILE);

                BigInteger fi = new BigInteger(kdcParams.split("fi:")[1]);
                BigInteger n = new BigInteger(sharedParams.split("n:")[1].split("v:")[0]);
                BigInteger v = new BigInteger(sharedParams.split("v:")[1]);
                BigInteger j = new BigInteger(alicesParams.split("Ja:")[1]);

                BigInteger s = KDC.generateS(v, fi);
                BigInteger sa = KDC.generateSA(n, j, s);

                alicesParams = "Ja:" + j + "\n" + "Sa:" + sa;
                writeInFile(ALICES_PARAMS_FILE, alicesParams);
                System.out.println("Alice's secret parameter Sa were generated in AlicesParams.txt");
                break;
            }
            case 3: {
                System.out.println("Alice select random secrete integer r and computes x");
                String alicesParams = readFromFile(ALICES_PARAMS_FILE);
                String sharedParams = readFromFile(SHARED_FILE);
                BigInteger sa = new BigInteger(alicesParams.split("Sa:")[1]);
                BigInteger n = new BigInteger(sharedParams.split("n:")[1].split("v:")[0]);
                BigInteger v = new BigInteger(sharedParams.split("v:")[1]);
                BigInteger ja = new BigInteger(alicesParams.split("Ja:")[1].split("Sa:")[0]);

                BigInteger r = Alice.generateR(n);
                BigInteger x = Alice.calculateX(r, v, n);

                alicesParams = "Ja:" + ja + "\n" + "Sa:" + sa + "\n" + "r:" + r + "\n" + "x:" + x;
                writeInFile(ALICES_PARAMS_FILE, alicesParams);
                System.out.println("Alice's parameters r and x were generated in AlicesParams.txt");
                break;
            }
            case 4: {
                System.out.println("Alice send pair (Ia, x) to Bob");
                String alicesParams = readFromFile(ALICES_PARAMS_FILE);
                String ia = readFromFile(ALICES_IDENTIFIER_FILE);
                BigInteger x = new BigInteger(alicesParams.split("x:")[1]);
                String bobsParams = "x:" + x;
                writeInFile(BOBS_PARAMS_FILE, bobsParams);
                writeInFile(BOB_FILE, ia);
                System.out.println("Alice's signature Ia was sent in Bob.txt, x was sent in BobsParams.txt");
                break;
            }
            case 5: {
                System.out.println("Bob select and send e to Alice");

                String bobsParams = readFromFile(BOBS_PARAMS_FILE);
                String alicesParams = readFromFile(ALICES_PARAMS_FILE);
                String sharedParams = readFromFile(SHARED_FILE);
                BigInteger x = new BigInteger(bobsParams.split("x:")[1]);
                BigInteger v = new BigInteger(sharedParams.split("v:")[1]);
                BigInteger ja = new BigInteger(alicesParams.split("Ja:")[1].split("Sa:")[0]);
                BigInteger sa = new BigInteger(alicesParams.split("Sa:")[1].split("r:")[0]);
                BigInteger r = new BigInteger(alicesParams.split("r:")[1].split("x:")[0]);

                BigInteger e = Bob.generateE(v);
                bobsParams = "x:" + x + "\n" + "e:" + e;
                alicesParams = "Ja:" + ja + "\n" + "Sa:" + sa + "\n" + "r:" + r + "\n" + "x:" + x + "\n" + "e:" + e;
                writeInFile(BOBS_PARAMS_FILE, bobsParams);
                writeInFile(ALICES_PARAMS_FILE, alicesParams);
                System.out.println("Bobs parameter e was generated in BobsParams.txt and send to Alice in AlicesParams.txt");
                break;
            }
            case 6: {
                System.out.println("Alice computes and send to Bob y");

                String bobsParams = readFromFile(BOBS_PARAMS_FILE);
                String alicesParams = readFromFile(ALICES_PARAMS_FILE);
                String sharedParams = readFromFile(SHARED_FILE);
                BigInteger sa = new BigInteger(alicesParams.split("Sa:")[1].split("r:")[0]);
                BigInteger n = new BigInteger(sharedParams.split("n:")[1].split("v:")[0]);
                BigInteger r = new BigInteger(alicesParams.split("r:")[1].split("x:")[0]);
                BigInteger e = new BigInteger(alicesParams.split("e:")[1]);
                BigInteger ja = new BigInteger(alicesParams.split("Ja:")[1].split("Sa:")[0]);
                BigInteger x = new BigInteger(alicesParams.split("x:")[1].split("e:")[0]);
                BigInteger eb = new BigInteger(bobsParams.split("e:")[1]);
                BigInteger xb = new BigInteger(bobsParams.split("x:")[1].split("e:")[0]);

                BigInteger y = Alice.calculateY(r, sa, e, n);

                alicesParams = "Ja:" + ja + "\n" + "Sa:" + sa + "\n" + "r:" + r + "\n" + "x:" + x + "\n" +
                        "e:" + e + "\n" + "y:" + y;
                bobsParams = "x:" + xb + "\n" + "e:" + eb + "\n" + "y:" + y;
                writeInFile(BOBS_PARAMS_FILE, bobsParams);
                writeInFile(ALICES_PARAMS_FILE, alicesParams);
                System.out.println("Alices parameter y was generated in AlicesParams.txt and send to Bob in BobsParams.txt");
                break;
            }
            case 7: {
                System.out.println("Bob calculate redundant identifier Ja = f(Ia), " +
                        "where Ia - Alice unique identifier, f - a well-known hash function and computes z");

                String ia = readFromFile(BOB_FILE);
                String bobsParams = readFromFile(BOBS_PARAMS_FILE);
                String sharedParams = readFromFile(SHARED_FILE);
                BigInteger n = new BigInteger(sharedParams.split("n:")[1].split("v:")[0]);
                BigInteger v = new BigInteger(sharedParams.split("v:")[1]);
                BigInteger e = new BigInteger(bobsParams.split("e:")[1].split("y:")[0]);
                BigInteger y = new BigInteger(bobsParams.split("y:")[1]);
                BigInteger x = new BigInteger(bobsParams.split("x:")[1].split("e:")[0]);

                BigInteger ja = KDC.encode(ia, n);
                BigInteger z = Bob.calculateZ(ja, e, n, y, v);
                bobsParams = "x:" + x + "\n" + "e:" + e + "\n" + "y:" + y + "\n" + "Ja:" + ja + "\n" + "z:" + z;
                writeInFile(BOBS_PARAMS_FILE, bobsParams);
                System.out.println("Bobs parameter z was generated and Ja was calculated in BobsParams.txt");
                break;
            }
            case 8: {
                System.out.println("Bob check that z = x and z != 0");

                String bobsParams = readFromFile(BOBS_PARAMS_FILE);

                BigInteger x = new BigInteger(bobsParams.split("x:")[1].split("e:")[0]);
                BigInteger z = new BigInteger(bobsParams.split("z:")[1]);


                if (Bob.check(z, x)) {
                    System.out.println("It is Alice!");
                }
                else {
                    System.out.println("It is not Alice");
                }
                break;
            }
            default:{
                System.out.println("Error command, please chose correct command 0-8");
            }
        }
    }

    public static void writeInFile(String file, String str) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(str);
            fileWriter.close();
        }
        catch (IOException e) {
            System.out.println("Error writing in file");
        }
    }

    public static String readFromFile(String file) {
        String result = "";
        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNext()) {
                result += scanner.nextLine();
            }
            scanner.close();
        }
        catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return result;
    }
}
