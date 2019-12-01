import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class Main {
    private static final String SHARED_FILE = "Share.txt";
    private static final String ALICES_PAIR_FILE = "Alice.txt";
    private static final String BOBS_PAIR_FILE = "Bob.txt";
    private static final String CAROLS_PAIR_FILE = "Carol.txt";

    public static void main(String[] args) {

        System.out.println("Выберете команду:" +
                "\n0 - Сгенерировать общее p - БПЧ" +
                "\n1 - Сгенерировать пары ключей для Алисы, Боба и Кэрол" +
                "\n2 - Алиса шифрует все 52 сообщения своим открытым ключом и посылает их Бобу" +
                "\n3 - Боб перемешивает колоду и выбирает случайно 5 сообщений, шифрует их своим открытым ключом и обратно отправляет Алисе," +
                "\n4 - Боб отправляет Кэрол оставшиеся 47 сообщений" +
                "\n5 - Кэрол случайно выбирает 5 сообщений, шифрует их своим открытым ключом и отправляет Алисе" +
                "\n6 - Алиса расшифровывает полученные от Боба сообщения и отправляет их Бобу" +
                "\n7 - Алиса расшифровывает полученные от Кэрол сообщения и отправляет их Кэрол" +
                "\n8 - Кэрол случайно выбрает 5 сообщений из оставшейся колоды и посылает их Алисе" +
                "\n9 - Боб, Алиса и Кэрол расшифровывают сообщения своими закрытыми ключами");

        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        start(a);
    }

    public static void start(int a) {

        switch (a) {
            case 0: {
                System.out.println("Генерация p - БПЧ");

                BigInteger p = Help.generatePrime();

                String sharedParams = "p:" + p;

                Help.writeInFile(SHARED_FILE, Arrays.asList(sharedParams));
                System.out.println("p - сгенерированно!");
                break;
            }
            case 1: {
                System.out.println("Генерация пар ключей для Алисы, Боба и Кэрол");

                List<String> sharedParams = Help.readFromFile(SHARED_FILE);

                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);

                BigInteger eA;
                do {
                    eA = BigInteger.probablePrime(p.bitLength() - 1, new SecureRandom()).mod(p);
                } while (!eA.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE));

                BigInteger dA = eA.modInverse(p.subtract(BigInteger.ONE));

                BigInteger eB;
                do {
                    eB = BigInteger.probablePrime(p.bitLength() - 1, new SecureRandom()).mod(p);
                } while (!eB.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE));

                BigInteger dB = eB.modInverse(p.subtract(BigInteger.ONE));

                BigInteger eC;
                do {
                    eC = BigInteger.probablePrime(p.bitLength() - 1, new SecureRandom()).mod(p);
                } while (!eC.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE));

                BigInteger dC = eC.modInverse(p.subtract(BigInteger.ONE));

                List<String> alicesPair = new ArrayList<>();
                List<String> bobsPair = new ArrayList<>();
                List<String> carolsPair = new ArrayList<>();
                alicesPair.add("eA:" + eA);
                alicesPair.add("dA:" + dA);
                bobsPair.add("eB:" + eB);
                bobsPair.add("dB:" + dB);
                carolsPair.add("eC:" + eC);
                carolsPair.add("dC:" + dC);

                Help.writeInFile(ALICES_PAIR_FILE, alicesPair);
                Help.writeInFile(BOBS_PAIR_FILE, bobsPair);
                Help.writeInFile(CAROLS_PAIR_FILE, carolsPair);

                System.out.println("Пары сгенерированны!");
                break;
            }
            case 2: {
                List<String> sharedParams = Help.readFromFile(SHARED_FILE);
                List<String> alicesPair = Help.readFromFile(ALICES_PAIR_FILE);
                BigInteger eA = new BigInteger(alicesPair.get(0).split(":")[1]);
                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);
                List<List<String>> message = new ArrayList<>();

                for (int i = 1; i <= 52; i++) {
                    Help.convert(p, i + "A.txt");
                }

                for (int i = 1; i <= 52; i++) {
                    message.add(Help.readCard(i + "A.txt"));
                }
                for (int i = 0; i < message.size(); i++) {
                    for (int j = 0; j < message.get(i).size(); j++) {
                        message.get(i).set(j, new BigInteger(message.get(i).get(j)).modPow(eA, p).toString());
                    }
                }

                for (int i = 1; i <= 52; i++) {
                    Help.writeCard(message.get(i - 1), i + "A.txt");
                    File file = new File(i + "A.txt");
                    file.renameTo(new File(i + "B.txt"));
                }
                break;
            }
            case 3: {
                List<String> bobsPair = Help.readFromFile(BOBS_PAIR_FILE);
                List<String> sharedParams = Help.readFromFile(SHARED_FILE);

                BigInteger eB = new BigInteger(bobsPair.get(0).split(":")[1]);
                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);

                List<List<String>> cards = new ArrayList<>();

                for (int i = 1; i <= 52; i++) {
                    cards.add(Help.readCard(i + "B.txt"));
                }

                Collections.shuffle(cards);

                for (int i = 1; i <= 52; i++) {
                    Help.writeCard(cards.get(i - 1), i + "B.txt");
                }

                Random random = new Random();

                for (int i = 0; i < 5; i++) {

                    int r = 1 + random.nextInt(cards.size() - 1);
                    File file = new File(r + "B.txt");
                    if (file.exists()) {
                        List<String> crd = Help.readCard(r + "B.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(eB, p).toString());
                        }
                        Help.writeCard(crd, r + "B.txt");
                        file.renameTo(new File(r + "AfromB.txt"));
                    } else {
                      i--;
                    }
                }
                break;
            }
            case 4: {
                for (int i = 1; i <= 52; i++) {
                    File file = new File(i + "B.txt");
                    if (file.exists()) {
                        file.renameTo(new File(i + "C.txt"));
                    }
                }
                break;
            }
            case 5: {
                List<String> carolsPair = Help.readFromFile(CAROLS_PAIR_FILE);
                List<String> sharedParams = Help.readFromFile(SHARED_FILE);

                BigInteger eC = new BigInteger(carolsPair.get(0).split(":")[1]);
                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);

                List<List<String>> cards = new ArrayList<>();

                for (int i = 1; i <= 52; i++) {
                    File file = new File(i + "C.txt");
                    if (file.exists()) {
                        cards.add(Help.readCard(i + "C.txt"));
                    }
                }

                Random random = new Random();

                for (int i = 0; i < 5; i++) {
                    int r = random.nextInt(cards.size());
                    File f = new File(r + "C.txt");
                    if (f.exists()) {
                        List<String> crd = Help.readCard(r + "C.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(eC, p).toString());
                        }
                        Help.writeCard(crd, r + "C.txt");
                        f.renameTo(new File(r + "AfromC.txt"));
                    }
                    else {
                        i--;
                    }
                }
                break;
            }
            case 6: {
                List<String> alicesPair = Help.readFromFile(ALICES_PAIR_FILE);
                List<String> sharedParams = Help.readFromFile(SHARED_FILE);

                BigInteger dA = new BigInteger(alicesPair.get(1).split(":")[1]);
                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);

                for (int i = 1; i <= 52; i++) {
                    File fc = new File(i + "AfromB.txt");
                    if (fc.exists()) {
                        List<String> crd = Help.readCard(i + "AfromB.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(dA, p).toString());
                        }
                        Help.writeCard(crd, i + "AfromB.txt");
                        fc.renameTo(new File(i + "BfromA.txt"));
                    }
                }
                break;
            }
            case 7: {
                List<String> alicesPair = Help.readFromFile(ALICES_PAIR_FILE);
                List<String> sharedParams = Help.readFromFile(SHARED_FILE);

                BigInteger dA = new BigInteger(alicesPair.get(1).split(":")[1]);
                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);

                for (int i = 1; i <= 52; i++) {
                    File fb = new File(i + "AfromC.txt");
                    if (fb.exists()) {
                        List<String> crd = Help.readCard(i + "AfromC.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(dA, p).toString());
                        }
                        Help.writeCard(crd, i + "AfromC.txt");
                        fb.renameTo(new File(i + "CfromA.txt"));
                    }
                }
                break;
            }
            case 8: {
                Random random = new Random();
                for (int i = 0; i < 5; i++) {
                    int r = random.nextInt(52);
                    File f = new File(r + "C.txt");
                    if (f.exists()) {
                        f.renameTo(new File(r + "AfromC.txt"));
                    }
                    else {
                        i--;
                    }
                }
                break;
            }
            case 9: {
                List<String> alicesPair = Help.readFromFile(ALICES_PAIR_FILE);
                List<String> bobsPair = Help.readFromFile(BOBS_PAIR_FILE);
                List<String> carolsPair = Help.readFromFile(CAROLS_PAIR_FILE);
                List<String> sharedParams = Help.readFromFile(SHARED_FILE);

                BigInteger dA = new BigInteger(alicesPair.get(1).split(":")[1]);
                BigInteger dB = new BigInteger(bobsPair.get(1).split(":")[1]);
                BigInteger dC = new BigInteger(carolsPair.get(1).split(":")[1]);
                BigInteger p = new BigInteger(sharedParams.get(0).split(":")[1]);

                for (int i = 1; i <= 52; i++) {
                    File fa = new File(i + "AfromC.txt");
                    File fb = new File(i + "BfromA.txt");
                    File fc = new File(i + "CfromA.txt");
                    if (fa.exists()) {
                        List<String> crd = Help.readCard(i + "AfromC.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(dA, p).toString());
                        }
                        Help.dec(crd, i + "AfromC.txt");
                    }
                    if (fb.exists()) {
                        List<String> crd = Help.readCard(i + "BfromA.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(dB, p).toString());
                        }
                        Help.dec(crd, i + "BfromA.txt");
                    }
                    if (fc.exists()) {
                        List<String> crd = Help.readCard(i + "CfromA.txt");
                        for (int j = 0; j < crd.size(); j++) {
                            crd.set(j, new BigInteger(crd.get(j)).modPow(dC, p).toString());
                        }
                        Help.dec(crd, i + "CfromA.txt");
                    }
                }
                break;
            }
            default:{
                System.out.println("Error command, please chose correct command 0-9");
            }
        }
    }
}
