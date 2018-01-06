package com.fengda.app.db;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.x;

public class XDBUtil {
	private static DaoConfig daoConfig;

	public static DaoConfig getDaoConfig() {
	//	File file = new File(Environment.getExternalStorageDirectory().getPath());
		if (daoConfig == null) {
			daoConfig = new DaoConfig().setDbName("fengda.db")// 创建数据库的名称
					//.setDbDir(file)//设置数据库路径，默认存储在app的私有目录
					.setDbVersion(1)// 数据库版本号
					.setAllowTransaction(true)// 设置允许开启事务
					.setDbOpenListener(new DbManager.DbOpenListener() {
						@Override
						public void onDbOpened(DbManager db) {
							// 开启WAL, 对写入加速提升巨大
							db.getDatabase().enableWriteAheadLogging();
						}
					}).setDbUpgradeListener(new DbUpgradeListener() {
						@Override
						public void onUpgrade(DbManager db, int oldVersion,
                                              int newVersion) {
							// db.addColumn(...);
							// db.dropTable(...);
							// ...
							// or
							// db.dropDb();
						}
					});
		}
		return daoConfig;
	}

	DaoConfig m_daoConfig = new DaoConfig()
    .setDbName("fengda.db")
    .setDbVersion(1)
    .setAllowTransaction(true)// 设置允许开启事务
					.setDbOpenListener(new DbManager.DbOpenListener() {
						@Override
						public void onDbOpened(DbManager db) {
							// 开启WAL, 对写入加速提升巨大
							db.getDatabase().enableWriteAheadLogging();
						}
					})
    .setDbUpgradeListener(new DbUpgradeListener() {//对应版本升级时应做的工作
        @Override
        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
            // db.addColumn(...);
            // db.dropTable(...);
            // ...
        }
    });
	
	 private static class SingletonHolder {
	        private static XDBUtil instance = new XDBUtil();
	    }

	    /**
	     * 私有的构造函数,防止外部构造
	     */
	    private XDBUtil() {
	    }

	    /**
	     * 单例的唯一对外访问接口，这种单例写法，既能保证多线程访问安全，又不会太影响效率
	     * @return 自身
	     */
	    public static XDBUtil getInstance() {
	        return SingletonHolder.instance;
	    }

	    /**
	     * 方便外部创建数据库链接时使用（部分数据结构对象执行关联查询时需要用到）
	     * @return
	     */
	    public DbManager getDbManager(){
	        return x.getDb(m_daoConfig);
	    }
	
	
}
