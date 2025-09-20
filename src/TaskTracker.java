import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskTracker {


    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try{
            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length != 2) {
                        System.out.println("Error: please provide a description");
                        return;
                    }
                    addTask(args[1]);
                    break;
                case "update":
                    if (args.length != 3) {
                        System.out.println("Error: please provide task ID and a description");
                        return;
                    }
                    updateTask(Integer.parseInt(args[1]), args[2]);
                    break;
                case "delete":
                    if (args.length != 2) {
                        System.out.println("Error: please provide task ID");
                        return;
                    }
                    deleteTask(Integer.parseInt(args[1]));
                    break;
                case "mark-in-progress":
                    if (args.length != 2) {
                        System.out.println("Error: please provide task ID and a description");
                        return;
                    }
                    markTask(Integer.parseInt(args[1]), "in-progress");
                    break;
                case "mark-done":
                    if (args.length != 2) {
                        System.out.println("Error: please provide task ID and a description");
                        return;
                    }
                    markTask(Integer.parseInt(args[1]), "done");
                    break;
                case "list":
                    if (args.length == 1) {
                        listAll();
                    }
                    else{
                        listByStatus(args[2]);
                    }
                default:
                    System.out.println("Error: unknown command '" + args[0] + "'");
                    printUsage();
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listByStatus(String arg) {
    }

    private static void listAll() {
    }

    private static void markTask(int i, String s) {
    }

    private static void deleteTask(int i) {
    }

    private static void updateTask(int i, String arg) {
    }

    private static void addTask(String description) throws IOException {

        List<Task> tasks = loadAllTasks();
        int newId = getNextId(tasks);
        Task newTask = new Task(newId, description, "todo",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        tasks.add(newTask);
        saveTasks(tasks);

    }

    private static void saveTasks(List<Task> tasks) throws IOException {
        String json = toJsonArray(tasks);
        Files.write(Paths.get("tasks.json"), json.getBytes());
    }

    private static String toJsonArray(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("  ").append(toJsonObject(tasks.get(i)));
            if (i != tasks.size() - 1) {
                sb.append(",\n");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonObject(Task task) {
        return String.format(
                "{\"id\": %d, \"description\": \"%s\", \"status\": \"%s\", \"createdAt\": \"%s\", \"updatedAt\": \"%s\" }",
                task.getId(), task.getDescription(), task.getStatus(), task.getCreatedAt(), task.getUpdatedAt()
        );
    }

    private static int getNextId(List<Task> tasks) {
        int maxId = 0;
        for (Task task : tasks) {
            if (task.getId() > maxId) maxId = task.getId();
            System.out.println(task.toString());
        }
        return maxId + 1;
    }

    private static List<Task> loadAllTasks() throws IOException {

        Path filePath = Paths.get("tasks.json");
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        String content = Files.readString(filePath);
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return parseJsonArray(content);

    }

    private static List<Task> parseJsonArray(String content) {
        List<Task> tasks = new ArrayList<>();
        content = content.trim();
        if(content.startsWith("[")) content = content.substring(1);
        if(content.endsWith("]")) content = content.substring(0, content.length()-1);
        if(content.isEmpty()) return tasks;

        String[] taskStrings = content.split("(?<=}),\\s*(?=\\{)");

        for(String taskString : taskStrings) {
            taskString = taskString.trim();
            Task task = parseJsonObject(taskString);
            tasks.add(task);
        }
        return tasks;
    }

    private static Task parseJsonObject(String taskString) {
        taskString = taskString.replaceAll("[{}]", "").trim();

        // split on commas that are not inside quotes
        String[] pairs = taskString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        Task task = new Task();

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            switch (key) {
                case "id":
                    task.setId(Integer.parseInt(value));
                    break;
                case "description":
                    task.setDescription(value);
                    break;
                case "status":
                    task.setStatus(value);
                    break;
                case "createdAt":
                    task.setCreatedAt(value);
                    break;
                case "updatedAt":
                    task.setUpdatedAt(value);
                    break;
            }
        }
        return task;
    }


    private static void printUsage() {
        System.out.println("Task Tracker CLI");
        System.out.println("Usage:");
        System.out.println("  java TaskTracker add \"<description>\"");
        System.out.println("  java TaskTracker update <id> \"<description>\"");
        System.out.println("  java TaskTracker delete <id>");
        System.out.println("  java TaskTracker mark-in-progress <id>");
        System.out.println("  java TaskTracker mark-done <id>");
        System.out.println("  java TaskTracker list");
        System.out.println("  java TaskTracker list [done|todo|in-progress]");
    }


}