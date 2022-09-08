package managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager getManager() {
        return new InMemoryTaskManager();
    }
}