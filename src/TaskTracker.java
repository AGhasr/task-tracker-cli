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
                        System.out.println("Error: please provide task ID");
                        return;
                    }
                    markTask(Integer.parseInt(args[1]), "in-progress");
                    break;
                case "mark-done":
                    if (args.length != 2) {
                        System.out.println("Error: please provide task ID");
                        return;
                    }
                    markTask(Integer.parseInt(args[1]), "done");
                    break;
                case "list":
                    if (args.length == 1) {
                        listAll();
                    }
                    else{
                        listByStatus(args[1]);
                    }
                    break;
                default:
                    System.out.println("Error: unknown command '" + args[0] + "'");
                    printUsage();
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listByStatus(String status) throws IOException {
        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null) {
            System.out.println("Error: unknown status '" + status + "'");
            return;
        }
        List<Task> tasks = loadAllTasks();
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus().equals(normalizedStatus))
                .toList();
        if (filteredTasks.isEmpty()) {
            System.out.println("No " + normalizedStatus + " tasks found");
        }
        System.out.println("\n" + normalizedStatus + " tasks: ");
        System.out.println("-----------");
        for (Task task : filteredTasks) {
            printTask(task);
        }
    }

    private static String normalizeStatus(String status) {
        return switch (status.toLowerCase()) {
            case "in-progress", "inprogress" -> "in-progress";
            case "done" -> "done";
            case "todo" -> "todo";
            default -> null;
        };
    }

    private static void listAll() throws IOException {
        List<Task> tasks = loadAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }

        System.out.println("\nAll tasks:");
        System.out.println("--------");
        for (Task task : tasks) {
            printTask(task);
        }
    }

    private static void printTask(Task task) {
        String statusIcon = getStatusIcon(task.getStatus());
        System.out.printf("[%d] %s %s (Created: %s, Updated: %s)%n",
                task.getId(),
                statusIcon,
                task.getDescription(),
                task.getCreatedAt(),
                task.getUpdatedAt())
                ;
    }

    private static String getStatusIcon(String status) {
        return switch (status) {
            case "in-progress" -> "[~]";
            case "done" -> "[✓]";
            case "todo" -> "[ ]";
            default -> "[?]";
        };
    }

    private static void markTask(int id, String status) throws IOException {
        List<Task> tasks = loadAllTasks();
        Task task = findTaskById(tasks, id);
        if (task == null) {
            System.out.println("Error: task not found");
            return;
        }
        task.setStatus(status);
        task.setUpdatedAt(getCurrentTimestamp());
        saveTasks(tasks);
        System.out.println("Task " + id + " marked as " + status + " successfully.");
    }

    private static void deleteTask(int id) throws IOException {
        List<Task> tasks = loadAllTasks();
        Task task = findTaskById(tasks ,id);

        if (task == null) {
            System.out.println("Error: task not found");
            return;
        }
        tasks.remove(task);
        saveTasks(tasks);
        System.out.println("Task deleted successfully.");
    }

    private static void updateTask(int id, String description) throws IOException {
        List<Task> tasks = loadAllTasks();
        Task task = findTaskById(tasks, id);

        if (task == null) {
            System.out.println("Error: task not found");
            return;
        }

        task.setDescription(description);
        task.setUpdatedAt(getCurrentTimestamp());

        saveTasks(tasks);

    }

    private static Task findTaskById(List<Task> tasks, int id) {

        return tasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
    }

    private static void addTask(String description) throws IOException {

        List<Task> tasks = loadAllTasks();
        int newId = getNextId(tasks);
        Task newTask = new Task(newId, description, "todo",
                getCurrentTimestamp(),
                getCurrentTimestamp());
        tasks.add(newTask);
        saveTasks(tasks);

    }

    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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