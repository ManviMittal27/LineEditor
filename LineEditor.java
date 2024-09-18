import java.io.*;
import java.util.*;

public class LineEditor {
    private static final int MAX_LINES = 25;
    private List<String> buffer = new ArrayList<>();
    private String fileName;
    private String directoryName;
    private File file;

    public LineEditor(String fileName, String directoryName) {
        this.fileName = fileName;
        this.directoryName = directoryName;
        this.file = new File(directoryName, fileName);
        openFile();
    }

    public LineEditor(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
        openFile();
    }

    public LineEditor() {
        this.fileName = "file.txt";
        this.file = new File(fileName);
        openFile();
    }

    private void openFile() {
        try {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null && buffer.size() < MAX_LINES) {
                    buffer.add(line);
                }
                reader.close();
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String line : buffer) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertLine(int lineNumber, String lineContent) {
        if (lineNumber >= 0 && lineNumber <= buffer.size() && buffer.size() < MAX_LINES) {
            buffer.add(lineNumber, lineContent);
        } else {
            System.out.println("Invalid line number or buffer is full.");
        }
    }

    public void updateLine(int lineNumber, String newContent) {
        if (lineNumber >= 0 && lineNumber < buffer.size()) {
            buffer.set(lineNumber, newContent);
        } else {
            System.out.println("Invalid line number.");
        }
    }

    public void deleteLine(int lineNumber) {
        if (lineNumber >= 0 && lineNumber < buffer.size()) {
            buffer.remove(lineNumber);
        } else {
            System.out.println("Invalid line number.");
        }
    }

    public void displayBuffer() {
        for (int i = 0; i < buffer.size(); i++) {
            System.out.println("[Line " + (i + 1) + "]: " + buffer.get(i));
        }
    }

    public List<Integer> searchWord(String word) {
        List<Integer> foundLines = new ArrayList<>();
        for (int i = 0; i < buffer.size(); i++) {
            if (buffer.get(i).contains(word)) {
                foundLines.add(i);
            }
        }
        if (foundLines.isEmpty()) {
            System.out.println("Word not found.");
        } else {
            for (int line : foundLines) {
                System.out.println("Word found at line " + (line + 1));
            }
        }
        return foundLines;
    }

    public void insertWord(int lineNumber, int position, String word) {
        if (lineNumber >= 0 && lineNumber < buffer.size()) {
            String line = buffer.get(lineNumber);
            if (position >= 0 && position <= line.length()) {
                String updatedLine = line.substring(0, position) + word + line.substring(position);
                buffer.set(lineNumber, updatedLine);
            } else {
                System.out.println("Invalid position.");
            }
        } else {
            System.out.println("Invalid line number.");
        }
    }

    public void deleteWord(int lineNumber, String word) {
        if (lineNumber >= 0 && lineNumber < buffer.size()) {
            String line = buffer.get(lineNumber);
            buffer.set(lineNumber, line.replaceFirst(word, ""));
        } else {
            System.out.println("Invalid line number.");
        }
    }

    private void openNewTerminal() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                command = "cmd /c start cmd";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                command = "gnome-terminal";
                // You might need to adjust the command for other terminal types on Unix-like systems
            } else {
                System.out.println("Unsupported OS for opening a new terminal.");
                return;
            }
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LineEditor editor;
        if (args.length == 0) {
            editor = new LineEditor();
        } else if (args.length == 1) {
            editor = new LineEditor(args[0]);
        } else if (args.length == 2) {
            editor = new LineEditor(args[0], args[1]);
        } else {
            System.out.println("Too many arguments provided.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String command;
        while (true) {
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            String[] parts = command.split(" ", 2);
            switch (parts[0]) {
                case "insert":
                    String[] insertParts = parts[1].split(" ", 2);
                    editor.insertLine(Integer.parseInt(insertParts[0]) - 1, insertParts[1]);
                    break;
                case "update":
                    String[] updateParts = parts[1].split(" ", 2);
                    editor.updateLine(Integer.parseInt(updateParts[0]) - 1, updateParts[1]);
                    break;
                case "delete":
                    editor.deleteLine(Integer.parseInt(parts[1]) - 1);
                    break;
                case "display":
                    editor.displayBuffer();
                    break;
                case "search":
                    editor.searchWord(parts[1]);
                    break;
                case "insertword":
                    String[] insertWordParts = parts[1].split(" ", 3);
                    editor.insertWord(Integer.parseInt(insertWordParts[0]) - 1, Integer.parseInt(insertWordParts[1]), insertWordParts[2]);
                    break;
                case "deleteword":
                    String[] deleteWordParts = parts[1].split(" ", 2);
                    editor.deleteWord(Integer.parseInt(deleteWordParts[0]) - 1, deleteWordParts[1]);
                    break;
                case "save":
                    editor.saveFile();
                    break;
                case "exit":
                    editor.saveFile();
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }
}
