import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static ZooKeeper zk;

    private static ZkConnector zkc;

    private static List<String> znodeList = new ArrayList<String>();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // Это мой кастомный класс. Его создаем первым, так как на нем все основанно
        zkc = new ZkConnector();
        // Это строка должна идти перед работой с ZooKeeper, тут мы пытаемся подключится к ZooKeeper и получаем экземпляр класса
        zk = zkc.connect("localhost");
        // Эта строка создаёт Node в ZooKeeper и сохраняем там данные (Если нода уже создана, то выбросится исключение)
        //zkc.create("/myNode",new String("It's my node!").getBytes());

        //Стандартный метод получения детей указанной ноды, возращает список строк
        znodeList = zk.getChildren("/",true);

        for(String s : znodeList) {
            System.out.println(s);
        }

        zkc.setConfig(new String(zkc.read("/myNode")));
        //Метод для обновления хранящихся данных по заданной ноде
        zkc.update("/myNode",new String("Update Node").getBytes());
        Thread.sleep(2000);
        System.out.println(zkc.getConfig());
        //Закрытие соединения с Zookeeper
        zkc.close();
    }

}
