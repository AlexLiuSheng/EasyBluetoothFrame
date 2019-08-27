<p align=center>
<img src='https://github.com/AlexLiuSheng/EasyBluetoothFrame/blob/master/ui/logo.png' width=20%/>
 </p>
 <p align=center>

<a href="https://jitpack.io/#AlexLiuSheng/EasyBluetoothFrame">
    <img src="https://jitpack.io/v/AlexLiuSheng/EasyBluetoothFrame.svg">
</a>
<a href="https://android-arsenal.com/api?level=14">
    <img src="https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat">
</a>
<a href="https://developer.android.com/index.html">
    <img src="https://img.shields.io/badge/platform-android-brightgreen.svg">
</a>
<a href="https://github.com/AlexLiuSheng/EasyBluetoothFrame/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/license-Apache%202.0-blue.svg">
</a>
</p>

 
 
## EasyBluetoothFrame
这是一个适用于经典蓝牙通讯的快速开发框架。


 
 
 
### 框架适用范围
   本框架只适用于支持蓝牙3.0协议的设备进行数据连接传输，也就是通常说的经典蓝牙，通常手机与手机之间的连接都属于经典蓝牙模式范畴，而一般连接外设耳机等设备，大多
   属于BLE蓝牙（低功耗蓝牙），这两种蓝牙除了名字有相同之外，通信方式、原理、协议完全不一样。
  
### 导入
> **version=**[![](https://jitpack.io/v/AlexLiuSheng/EasyBluetoothFrame.svg)](https://jitpack.io/#AlexLiuSheng/EasyBluetoothFrame)
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
dependencies {
      implementation 'com.github.AlexLiuSheng:EasyBluetoothFrame:version'
}

```
### 蓝牙连接流程
---
 经典蓝牙采用C/S模式，所以同一个设备，只能作为Client端或者Server端的其中一个，
 - 如果设备充当Server端，那么监听流程如下：
    - 蓝牙权限获取
    - 设置设备可被发现
    - 设置监听UUID
    - 注册为Server
    - 等待配对、连接、传输数据
    
 - 如果设备充当Client，本框架蓝牙的连接流程一般为：
    - 蓝牙权限获取
    - 扫描设备
    - 配对、绑定设备
    - 建立连接
    - 传输数据
   

### 蓝牙权限获取
 可以使用库`BluetoothPermissionHandler`来辅助获取蓝牙权限或者开发者自己动态获取权限，`BluetoothPermissionHandler`用法如下：
  ```
 private val permissionCallBack=BluetoothPermissionHandler(this,this)
    override fun onCreate(savedInstanceState: Bundle?) {
       permissionCallBack.start();
    }
    override fun permissionFailed() {
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionCallBack.onActivityResult(requestCode, resultCode, data)
    }
  override  fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCallBack.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onBlueToothEnabled() {


    }
```

### 初始化以及相关方法
---
   -  初始化操作可以放到任何地方
     `BleManager.getInstance().init(application)`
   -  使用前台service传输数据
      `BleManager.getInstance().setForegroundService(true)`
   - 如果使用ForegroundService模式，可自定义通知栏
    `BleManager.getInstance().setNotification(notification)`
   - 获取所有配对过的设备
     `BleManager.getInstance().getPairedDevices()`
   
## 连接
不管是服务端还是客户端都需要获取蓝牙权限，并且进行初始化BleManager。如果使用`BluetoothPermissionHandler`，应该在`onBlueToothEnabled`回调中做
扫描连接等操作。

### 设置为Server端
---
#### 设置UUID
默认使用`00001101-0000-1000-8000-00805F9B34FB`，当然可以自定义
```
BleManager.getInstance().setConnectionUUID()
```
#### 设置设备可被扫描发现
传入参数为可被发现的时间，单位秒
```
  BleManager.getInstance().enableDiscoverable(300)
```
#### 注册为Server
注册为Server端之后，当有设备连接成功后，会回调`connectSuccess`方法，我们可以保存回调回来的
`Connect`为全局，方便读写操作
```
  BleManager.getInstance().registerServerConnection(object: ConnectResultlistner {
                override fun connectSuccess(connect: Connect?) {
                    this@MainActivity.connect=connect
                    read()
                }

                override fun connectFailed(e: Exception?) {

                }

            })
  ```
### 设置为Client端
---
#### 扫描设备

配置扫描时间，然后扫描，在扫描回调中，通常操作是将回调的蓝牙设备添加到列表并显示，需要注意的是，回调的蓝牙设备可能会**重复**，因此需要手动去重
```
//配置扫描时间
 val config=ScanConfig(5000)
 BleManager.getInstance().scan(config,object :ScanResultListener{
                override fun onDeviceFound(device: BluetoothDevice?) {
                  
                }

                override fun onFinish() {
                }

                override fun onError() {
                }
            })
```
   
#### 设置UUID
默认使用`00001101-0000-1000-8000-00805F9B34FB`，当然可以自定义
```
BleManager.getInstance().setConnectionUUID()
```

#### 配对(如果已经配对，跳过此步)
如果没有配对，需要先进行设备配对，如果已经配对，那么可以直接进行连接操作
判断是否已经配对，可以用`BleManager.getInstance().getPairedDevices()`返回的集合进行判断
配对代码如下：
```
    BleManager.getInstance().pin(data[p1],object:PinResultListener{
                    override fun paired(device: BluetoothDevice?) {
                        connect(context,data[p1])
                    }

                    override fun startPair(device: BluetoothDevice?) {
                        super.startPair(device)
                    }

                    override fun pairing(device: BluetoothDevice?) {
                        super.pairing(device)
                    }

                    override fun pairFailed(device: BluetoothDevice?) {
                        super.pairFailed(device)
                    }
                })
```

#### 连接Server
当有设备连接成功后，会回调`connectSuccess`方法，我们可以保存回调回来的
`Connect`为全局，方便读写操作
```
BleManager.getInstance().connect(d, object : ConnectResultlistner {
        override fun connectSuccess(connect: Connect?) {
        }

        override fun connectFailed(e: Exception?) {
        }
    })
```


### 传输数据
#### **NewFeature自定义一个完整数据包包头、包尾**
```
   connect?.setReadPacketVerifyListener(object : PacketDefineListener {
            override fun getPacketStart(): ByteArray {
                return start

            }

            override fun getPacketEnd(): ByteArray {
                return end

            }
        })
```
#### 读
```
  connect?.read(object: TransferProgressListener {
            override fun transferSuccess(bytes: ByteArray?) {
                bytes?.let { it1 ->
                    tvReceive.text=String(it1)
                }
                CLog.e("read string")
            }
            override fun transferFailed(msg:String) {
                t(msg)
            }
            override fun transfering(progress: Int) {
                CLog.e("read progress:$progress")
            }
        })
```
#### 写
```
 connect?.write(text.toByteArray(), object : TransferProgressListener {
            override fun transferSuccess(bytes: ByteArray?) {

            }

            override fun transferFailed(msg:String) {
                t(msg)
            }

            override fun transfering(progress: Int) {
                CLog.e("write progress:$progress")
            }
        })
```
### 断开连接
```
 BleManager.getInstance().destory()
```
## API Function
所有功能接口
```
    void init(Context context);
    void setConnectionUUID(String uuid);

    boolean isSupported();

    void setResultListener(ScanResultListener resultListener);

    Set<BluetoothDevice> getPairedDevices();

    void scan(ScanConfig scanConfig, ScanResultListener scanResultListener);

    void stopSearch();

    void connect(BluetoothDevice device, ConnectResultlistner connectResultlistner);
    void destory();
    void pin(BluetoothDevice device, PinResultListener resultListener);
    void cancelPin(BluetoothDevice device, ResultListener resultListener);
    void setServerConnectResultListener(ConnectResultlistner connectResultListener);
    void registerServerConnection(ConnectResultlistner connectResultListener);

    void setForegroundService(boolean foregroundService);
    void setNotification(Notification notification);
    void enableDiscoverable(long time);
```
## demo
<img src='https://github.com/AlexLiuSheng/EasyBluetoothFrame/blob/master/ui/gif.gif' width=20%/>

## TODO
  - [ ] 传输数据进度完善
## Last
- 详细适用请看demo，第一版可能还有些不完善,持续完善中
- 欢迎PR或Issues
- 如果使用过程中觉得还不错，请不要吝啬你的star,哈哈。
## License
---
Apache 2.0

