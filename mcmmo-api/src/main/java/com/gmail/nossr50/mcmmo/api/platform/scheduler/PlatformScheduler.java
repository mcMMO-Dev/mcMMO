package com.gmail.nossr50.mcmmo.api.platform.scheduler;

import java.util.function.Consumer;

public interface PlatformScheduler {

    TaskBuilder getTaskBuilder();

    Task scheduleTask(TaskBuilder taskBuilder);


    class TaskBuilder {
        Integer delay;
        Integer repeatTime;

        public boolean isAsync() {
            return isAsync;
        }

        public TaskBuilder setAsync(boolean async) {
            isAsync = async;
            return this;
        }

        boolean isAsync = false;
        Consumer<Task> task;

        public Integer getDelay() {
            return delay;
        }

        public TaskBuilder setDelay(Integer delay) {
            this.delay = delay;
            return this;
        }

        public Integer getRepeatTime() {
            return repeatTime;
        }

        public TaskBuilder setRepeatTime(Integer repeatTime) {
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
    }
}
