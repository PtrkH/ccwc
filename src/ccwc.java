import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ccwc {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                // Read from standard input
                handleInputStream(System.in);
            } else if (args.length == 1) {
                // Single argument could be a flag or file name
                handleSingleArgument(args[0]);
            } else if (args.length == 2) {
                // Two arguments should be a flag and a file name
                handleFileAndFlag(args[0], args[1]);
            } else {
                System.out.println("Invalid number of arguments.");
            }
        } catch (IOException e) {
            System.out.println("An I/O error occurred: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleInputStream(InputStream inputStream) throws IOException {
        byte[] inputData = readAllBytes(inputStream);

        long numberOfBytes = calculateNumberOfBytes(new ByteArrayInputStream(inputData));
        long numberOfWords = calculateNumberOfWords(new ByteArrayInputStream(inputData));
        long numberOfLines = calculateNumberOfLines(new ByteArrayInputStream(inputData));

        System.out.printf("%d %d %d%n", numberOfLines, numberOfWords, numberOfBytes);
    }

    private static void handleSingleArgument(String arg) throws IOException {
        if (isFlag(arg)) {
            // Argument is a flag, read from standard input
            long value = resolveFlag(arg, System.in);
            System.out.printf("%d%n", value);
        } else {
            // Argument should be a file name
            processFile(arg, null);
        }
    }

    private static void handleFileAndFlag(String flag, String fileName) throws IOException {
        if (!isFlag(flag)) {
            throw new IllegalArgumentException("First argument must be a flag.");
        }
        processFile(fileName, flag);
    }

    private static void processFile(String fileName, String flag) throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            System.out.println("File does not exist.");
            return;
        }

        try (InputStream fileStream = new FileInputStream(fileName)) {
            if (flag != null) {
                // Process file with the given flag
                long value = resolveFlag(flag, fileStream);
                System.out.printf("%d%n", value);
            } else {
                // No flag, process for all counts
                byte[] fileData = readAllBytes(fileStream);
                long numberOfBytes = calculateNumberOfBytes(new ByteArrayInputStream(fileData));
                long numberOfWords = calculateNumberOfWords(new ByteArrayInputStream(fileData));
                long numberOfLines = calculateNumberOfLines(new ByteArrayInputStream(fileData));
                System.out.printf("%d %d %d %s%n", numberOfLines, numberOfWords, numberOfBytes, fileName);
            }
        }
    }

    private static boolean isFlag(String arg) {
        return "-c".equals(arg) || "-w".equals(arg) || "-l".equals(arg) || "-m".equals(arg);
    }

    private static long resolveFlag(String flag, InputStream inputStream) throws IOException {
        switch (flag) {
            case "-c":
                return calculateNumberOfBytes(inputStream);
            case "-w":
                return calculateNumberOfWords(inputStream);
            case "-l":
                return calculateNumberOfLines(inputStream);
            case "-m":
                return calculateNumberOfCharacters(inputStream);
            default:
                throw new IllegalArgumentException("Invalid flag. Use -c, -w, -l, or -m.");
        }
    }

    private static long calculateNumberOfBytes(InputStream inputStream) {
        long numberOfBytes = 0;
        try {
            byte[] buffer = new byte[1024]; // A buffer to hold read data
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                numberOfBytes += bytesRead;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Handle the exception, maybe log it
            }
        }
        return numberOfBytes;
    }

    private static long calculateNumberOfWords(InputStream inputStream) {
        long numberOfWords = 0;
        try (java.util.Scanner scanner = new java.util.Scanner(inputStream)) {

            while (scanner.hasNext()) {
                scanner.next();
                numberOfWords++;
            }
        }
        return numberOfWords;
    }

    private static long calculateNumberOfLines(InputStream inputStream) {
        long numberOfLines = 0;
        try (java.util.Scanner scanner = new java.util.Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                numberOfLines++;
            }
        }
        return numberOfLines;
    }

    private static long calculateNumberOfCharacters(InputStream inputStream) {
        long numberOfCharacters = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.read() != -1) {
                numberOfCharacters++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return numberOfCharacters;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int length;
        while ((length = inputStream.read(data)) != -1) {
            buffer.write(data, 0, length);
        }
        return buffer.toByteArray();
    }
}