package com.gmail.nossr50.mcmmo.api.platform.scheduler;

import java.util.function.Consumer;

public interface PlatformScheduler {

    TaskBuilder getTaskBuilder();

    Task scheduleTask(TaskBuilder taskBuilder);


    abstract class TaskBuilder {
        Long delay;
        Long repeatTime;

        public boolean isAsync() {
            return isAsync;
        }

        public TaskBuilder setAsync(boolean async) {
            isAsync = async;
            return this;
        }

        boolean isAsync = false;
        Consumer<Task> task;

        public Long getDelay() {
            return delay;
        }

        public TaskBuilder setDelay(Long delay) {
            this.delay = delay;
            return this;
        }

        public Long getRepeatTime() {
            return repeatTime;
        }

        public TaskBuilder setRepeatTime(Long repeatTime) {
            this.repeatTime = repeatTime;
            return this;
        }

        public Consumer<Task> getTask() {
            return task;
        }

        public TaskBuilder setTask(Consumer<Task> task) {
            this.task = task;
            return this;
        }

        @Deprecated
        public TaskBuilder setTask(Runnable runnableTask) {
            this.setTask(task -> runnableTask.run());
            return this;
        }

        public abstract Task schedule();
    }
}
