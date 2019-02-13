import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkConnector {

    private String config;

    private ZooKeeper zk;

    private static int TIME = 5000;

    private CountDownLatch connSignal = new CountDownLatch(1);

    public ZooKeeper connect(String host) throws InterruptedException, IOException {
        zk = new ZooKeeper(host, TIME, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connSignal.countDown();
                }
            }
        });
        connSignal.await();
        return zk;
    }

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.create(path,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public byte[] read(String path) throws KeeperException, InterruptedException {
        return zk.getData(path, true, zk.exists(path, true));
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.setData(path,data,zk.exists(path,true).getVersion());
    }
    //Установка событие на изменение заданной ноды (Сработает только 1 раз)
    public void eventChange(final String path) throws KeeperException, InterruptedException {
        zk.getData(path, new Watcher() {
            public void process(WatchedEvent event) {
                if(event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        config = new String(read(path));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },zk.exists(path, true));
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        zk.delete(path,zk.exists(path,true).getVersion());
    }

    public void close() throws InterruptedException {
        zk.close();
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
