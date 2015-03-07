import com.henko.server.dao.RedirectInfoDao;
import com.henko.server.dao.connectionpool.HikariConnPool;
import com.henko.server.dao.impl.DaoFactory;
import com.henko.server.db.DBManager;
import com.henko.server.model.RedirectInfo;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.henko.server.dao.impl.DaoFactory.*;
import static junit.framework.Assert.assertEquals;

public class TestH2RedirectInfoDao {
    HikariConnPool pool = HikariConnPool.getConnPool();
    DaoFactory daoFactory = getDaoFactory(H2);
    RedirectInfoDao redirectInfoDao = daoFactory.getRedirectInfoDao();

    @Before
    public void setUp() throws SQLException {
        DBManager dbManager = new DBManager();
        dbManager.dropTables();
        dbManager.initialiseDB();
        initialiseDBData();
    }

    private void initialiseDBData() throws SQLException {
        Connection conn = pool.getConnection();
        Statement stmt = conn.createStatement();

        String insert1DataSQL = "INSERT INTO REDIRECTS (URL, R_COUNT) VALUES('google.com', 10);";
        String insert2DataSQL = "INSERT INTO REDIRECTS (URL, R_COUNT) VALUES('vk.com', 20);";
        String insert3DataSQL = "INSERT INTO REDIRECTS (URL, R_COUNT) VALUES('facebook.com', 30);";

        stmt.executeUpdate(insert1DataSQL);
        stmt.executeUpdate(insert2DataSQL);
        stmt.executeUpdate(insert3DataSQL);
    }

    @Test
    public void testSelectById() {
        RedirectInfo expected = new RedirectInfo(1, "google.com", 10);
        RedirectInfo actual = redirectInfoDao.selectById(1);

        assertEquals(expected, actual);
    }

    @Test
    public void testSelectByUrl() {
        RedirectInfo expected = new RedirectInfo(1, "google.com", 10);
        RedirectInfo actual = redirectInfoDao.selectByUrl("google.com");

        assertEquals(expected, actual);
    }

    @Test
    public void testSelectAll() {
        List<RedirectInfo> expected = new ArrayList<RedirectInfo>() {{
            add(new RedirectInfo(1, "google.com", 10));
            add(new RedirectInfo(2, "vk.com", 20));
            add(new RedirectInfo(3, "facebook.com", 30));
        }};

        List<RedirectInfo> actual = redirectInfoDao.selectAll();

        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateCountByUrl() {
        RedirectInfo expected = new RedirectInfo(1, "google.com", 11);
        redirectInfoDao.updateCountByUrl("google.com", 11);
        RedirectInfo actual = redirectInfoDao.selectByUrl("google.com");

        assertEquals(expected, actual);
    }

    @Test
    public void testBasePerformance() throws InterruptedException {
        /*Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    redirectInfoDao.selectByUrl("google.com");
                }
            }
        });
        thread1.start();
        thread1.join();

        System.out.println(1);
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    redirectInfoDao.selectAll();
                }
            }
        });
        thread2.start();
        thread2.join();

        System.out.println(2);*/
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    redirectInfoDao.updateCountByUrl("google.com", 100);
                }
            }
        });
        thread3.start();
        thread3.join();

        System.out.println(3);
    }
}
