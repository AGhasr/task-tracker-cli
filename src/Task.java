import java.util.Objects;

public class Task {
   private int id;
   private String description;
   private String status;
   private String createdAt;
   private String updatedAt;

   public Task(int id, String descString, String status, String createdAt, String updatedAt){
    this.id = id;
    this.description = descString;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
   }

   public Task() {

   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }

   public String getUpdatedAt() {
      return updatedAt;
   }

   public void setUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Task task = (Task) o;
      return id == task.id;
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(id);
   }

   @Override
   public String toString() {
      return "Task{" +
              "id=" + id +
              ", description='" + description + '\'' +
              ", status='" + status + '\'' +
              ", createdAt='" + createdAt + '\'' +
              ", updatedAt='" + updatedAt + '\'' +
              '}';
   }
}