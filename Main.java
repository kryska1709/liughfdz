import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите путь к файлу: ");
        String path = scanner.nextLine();

        System.out.print("Введите новое имя для класса: ");
        String newClassName = scanner.nextLine();

        try {
            String result = Files.readString(Path.of(path));
            String oldClassName = Path.of(path).getFileName().toString().replace(".java", "");

            // Удаление комментариев и сокращение пробелов
            result = removeCommentsAndTrimSpaces(result);

            // Замена имени класса
            result = result.replaceAll("\\b" + oldClassName + "\\b", newClassName);

            // Генерация уникальных идентификаторов и их замена
            Map<String, String> identifierMap = generateUniqueIdentifiers(result, newClassName);
            for (Map.Entry<String, String> entry : identifierMap.entrySet()) {
                result = result.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }

            // Запись результата в новый файл
            Files.writeString(Path.of(newClassName + ".java"), result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String removeCommentsAndTrimSpaces(String input) {
        // Удаление комментариев и сокращение пробелов
        return input.replaceAll("(/\\*.*?\\*/)|(//.*)", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static Map<String, String> generateUniqueIdentifiers(String code, String newClassName) {
        Set<String> identifiers = new HashSet<>();
        String regex = "\\b(?:int|double|String|boolean|float|char|long|byte|short|void|class)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\b";
        Matcher matcher = Pattern.compile(regex).matcher(code);

        // Заполнение списка уникальных идентификаторов
        while (matcher.find()) {
            String identifier = matcher.group(1);
            if (!identifier.equals(newClassName) && !identifier.equals("main")) {
                identifiers.add(identifier);
            }
        }

        return generateUniqueNames(identifiers);
    }

    private static Map<String, String> generateUniqueNames(Set<String> identifiers) {
        Map<String, String> map = new HashMap<>();
        Set<String> usedNames = new HashSet<>();
        Random random = new Random();

        for (String identifier : identifiers) {
            String newName;
            do {
                newName = generateRandomName(identifiers.size(), random);
            } while (usedNames.contains(newName));

            usedNames.add(newName);
            map.put(identifier, newName);
        }

        return map;
    }

    private static String generateRandomName(int identifierCount, Random random) {
        // Генерация уникального имени по количеству идентификаторов
        if (identifierCount <= 26){
            return String.valueOf((char) ('a' + random.nextInt(26)));
        } else {
            return "" + (char) ('a' + random.nextInt(26)) + (char) ('a' + random.nextInt(26));
        }
    }
}
