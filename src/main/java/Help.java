import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Help {

    private  static Map<Character, Integer> alph = new TreeMap<>();

    static {
        alph.put('0', 18);
        alph.put('1', 28);
        alph.put('2', 38);
        alph.put('3', 48);
        alph.put('4', 58);
        alph.put('5', 68);
        alph.put('6', 78);
        alph.put('7', 118);
        alph.put('8', 128);
        alph.put('9', 138);
        alph.put('а', 148);
        alph.put('б', 158);
        alph.put('в', 168);
        alph.put('г', 178);
        alph.put('д', 218);
        alph.put('е', 228);
        alph.put('ё', 238);
        alph.put('ж', 248);
        alph.put('з', 258);
        alph.put('и', 268);
        alph.put('й', 278);
        alph.put('к', 318);
        alph.put('л', 328);
        alph.put('м', 338);
        alph.put('н', 348);
        alph.put('о', 358);
        alph.put('п', 368);
        alph.put('р', 378);
        alph.put('с', 418);
        alph.put('т', 428);
        alph.put('у', 438);
        alph.put('ф', 448);
        alph.put('х', 458);
        alph.put('ц', 468);
        alph.put('ч', 478);
        alph.put('ш', 518);
        alph.put('щ', 528);
        alph.put('ь', 538);
        alph.put('ы', 548);
        alph.put('ъ', 558);
        alph.put('э', 568);
        alph.put('ю', 578);
        alph.put('я', 618);
        alph.put(' ', 628);
        alph.put(',', 638);
        alph.put('.', 648);
        alph.put('-', 658);
        alph.put('?', 668);
        alph.put('!', 678);
        alph.put('А', 718);
        alph.put('Б', 728);
        alph.put('В', 738);
        alph.put('Г', 748);
        alph.put('Д', 758);
        alph.put('Е', 768);
        alph.put('Ё', 778);
        alph.put('Ж', 1118);
        alph.put('З', 1128);
        alph.put('И', 1138);
        alph.put('Й', 1148);
        alph.put('К', 1158);
        alph.put('Л', 1168);
        alph.put('М', 1178);
        alph.put('Н', 1218);
        alph.put('О', 1228);
        alph.put('П', 1238);
        alph.put('Р', 1248);
        alph.put('С', 1258);
        alph.put('Т', 1268);
        alph.put('У', 1278);
        alph.put('Ф', 1318);
        alph.put('Х', 1328);
        alph.put('Ц', 1338);
        alph.put('Ч', 1348);
        alph.put('Ш', 1358);
        alph.put('Щ', 1368);
        alph.put('Ь', 1378);
        alph.put('Ы', 1418);
        alph.put('Ъ', 1428);
        alph.put('Э', 1438);
        alph.put('Ю', 1448);
        alph.put('Я', 1458);
    }

    public static void writeInFile(String file, List<String> str) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            for(String s: str) {
                fileWriter.write(s + "\n");
            }
            fileWriter.close();
        }
        catch (IOException e) {
            System.out.println("Error writing in file");
        }
    }

    public static List<String> readFromFile(String file) {
        List<String> result = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNext()) {
                result.add(scanner.nextLine());
            }
            scanner.close();
        }
        catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return result;
    }

    public static List<String> readCard(String file) {
        List<String> result = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new FileReader(file));
            while (scanner.hasNext()) {
                result.add(scanner.nextLine());
            }
            scanner.close();
        }
        catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return result;
    }

    public static void writeCard(List<String> list, String file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (String s: list) {
                fileWriter.write(s + "\n");
            }
            fileWriter.close();
        }
        catch (IOException e) {
            System.out.println("Error reading from file");
        }
    }

    public static BigInteger generatePrime() {
        Random random = new Random();
        int bitLength = 5 + random.nextInt(200);
        return BigInteger.probablePrime(bitLength, new Random());
    }

    public static void dec(List<String> list, String file) {
        try (FileWriter fileWriter = new FileWriter(file)) {

            String s = "";
            for (String ss: list) {
                s += ss;
            }
            String[] strings = s.split("8");
            for (int i = 0; i < strings.length; i++) {
                strings[i] = strings[i] + "8";
            }
            for (String str : strings) {
                for (Map.Entry<Character, Integer> pair : alph.entrySet()) {
                    if (pair.getValue() == Integer.parseInt(str)) {
                        fileWriter.write(pair.getKey());
                        break;
                    }
                }
            }
        }
        catch (IOException ex) {
            System.out.println("Ошибка записи в файл!");
        }
    }

    public static void convert(BigInteger n, String file) {
        String s = "";
        String mess = "";
        try (FileReader reader = new FileReader(file);
             Scanner scanner = new Scanner(reader)){
            while (scanner.hasNext()) {
                char[] ch = scanner.nextLine().toCharArray();
                for (Character character: ch)
                    for (Map.Entry<Character, Integer> pair: alph.entrySet()) {
                        if (pair.getKey().equals(character)) {
                            mess += pair.getKey();
                            s += String.valueOf(pair.getValue());
                            break;
                        }
                    }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<BigInteger> list = new ArrayList<>();
        BigInteger number = new BigInteger(s);
        if (number.compareTo(n) != -1) {
            while (!s.equals("")) {
                BigInteger maxx = new BigInteger(s.substring(0, 1));
                for (int i = 1; i < s.length() + 1; i++) {
                    if (new BigInteger(s.substring(0, i)).compareTo(n) != -1) {
                        s = s.substring(i - 1);
                        break;
                    }
                    if (maxx.compareTo(new BigInteger(s.substring(0, i))) == -1 && new BigInteger(s.substring(0, i)).compareTo(n) == -1)
                        maxx = new BigInteger(s.substring(0, i));
                    if (i == s.length()) {
                        s = "";
                    }
                }
                list.add(maxx);
            }
        }
        else {
            list.add(number);
        }
        List<String> res = list.stream().map(b -> b.toString()).collect(Collectors.toList());
        writeInFile(file, res);
    }
}
