package virtua.training.primeng.jsweet.todo.service;

import virtua.training.primeng.jsweet.todo.model.Task;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Manages tasks (in memory).
 * <p>
 * Created: 23 Oct 2015
 *
 * @author Kito D. Mann
 */
@ApplicationScoped
public class TaskService {

    private ConcurrentLinkedDeque<Task> queue;

    @Inject
    protected UserService userService;

    public TaskService() {
        System.out.println("created new taskservice");
        queue = new ConcurrentLinkedDeque<>();
    }

    @PostConstruct
    public void init() {
        add(new Task(null, userService.getSystemUser().getUserId(), "Read Angular2 docs", true));
        add(new Task(null, userService.getSystemUser().getUserId(), "Read JSweet docs", false));
        add(new Task(null, userService.getSystemUser().getUserId(), "Read Jersey docs", false));
    }

    public Task[] getTasks() {
        return queue.toArray(new Task[]{});
    }

    public void add(Task task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        queue.offerFirst(task);
    }

    public void update(Task task) {
        Optional<Task> result = get(task.getId());
        if (result.isPresent()) {
            Task currentTask = result.get();
            currentTask.setName(task.getName());
            currentTask.setCompleted(task.isCompleted());
            currentTask.setUserId(task.getUserId());
        } else {
            throw new NoSuchElementException("There is no task with the id " + task.getId());
        }
    }

    public Optional<Task> get(String id) {
        // Inefficient, but this is just a sample app....
        return queue.stream().filter((task) -> task.getId().equals(id)).findFirst();
    }

    public void remove(String id) {
        Optional<Task> result = get(id);
        if (result.isPresent()) {
            queue.remove(result.get());
        }
    }

    public void removeAll() {
        queue.clear();
    }
}

