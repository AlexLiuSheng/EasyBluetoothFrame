package com.allenliu.btdemo

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.allenliu.classicbt.BleManager
import com.allenliu.classicbt.BluetoothPermissionHandler
import com.allenliu.classicbt.CLog
import com.allenliu.classicbt.Connect
import com.allenliu.classicbt.listener.*
import com.allenliu.classicbt.scan.ScanConfig
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity(), BluetoothPermissionCallBack {
    private lateinit var list: ArrayList<BluetoothDevice>
    var connect: Connect? = null
    private val permissionCallBack = BluetoothPermissionHandler(this, this)
    override fun permissionFailed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionCallBack.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCallBack.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBlueToothEnabled() {

        BleManager.getInstance().init(application)
        BleManager.getInstance().setForegroundService(true)
        btn2.setOnClickListener {
            BleManager.getInstance().scan(ScanConfig(5000), object : ScanResultListener {
                override fun onDeviceFound(device: BluetoothDevice?) {
                    if (!isContained(device!!)) {
                        list.add(device)
                        recyclerview.adapter?.notifyDataSetChanged()
                    }
                }

                override fun onFinish() {
                }

                override fun onError() {
                }
            })
        }
        btn1.setOnClickListener {
            t("register server success.you can connnect device now.")
            registerServer()
        }
        btn3.setOnClickListener {
            write()
        }
        btnDiscoverable.setOnClickListener {
            BleManager.getInstance().enableDiscoverable(300)
        }

    }

    fun registerServer() {
        BleManager.getInstance().registerServerConnection(object : ConnectResultlistner {
            override fun disconnected() {
                t("bluetooth has disconnected")
                BleManager.getInstance().destory()
                registerServer()
            }

            override fun connectSuccess(connect: Connect?) {
                this@MainActivity.connect = connect
                read()
            }

            override fun connectFailed(e: Exception?) {

            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview.layoutManager = LinearLayoutManager(this)
        list = ArrayList()
        recyclerview.adapter = MyAdapter(this, list)
        permissionCallBack.start()
    }

    private fun isContained(result: BluetoothDevice): Boolean {
        if (result.name == null || "null".equals(result.name, ignoreCase = true))
            return true
        for (device in list) {
            if (result.address == device.address) {
                return true
            }
        }
        return false
    }

    fun read() {
        val a:Int= -0x146f1470
        val buffer=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(a)
        val b:Byte= 0x03
        val buffer2=ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(b)
        connect?.setReadPacketVerifyListener(object : PacketDefineListner {
            override fun getPacketStart(): ByteArray {
                return buffer.array()

            }

            override fun getPacketEnd(): ByteArray {
                return buffer2.array()

            }
        })
        connect?.read(object : TransferProgressListener {

            override fun transferSuccess(bytes: ByteArray?) {
                t("received message")
                bytes?.let { it1 ->
                    tvReceive.text = String(it1)
                }

                CLog.e("read string")
            }

            override fun transferFailed(msg: Exception) {
                msg.printStackTrace()
//                msg.message?.run {
//                    t(this)
//                }

            }

            override fun transfering(progress: Int) {
                CLog.e("read progress:$progress")
            }
        })
    }

    private fun write() {
        val text = et.text.toString() + "Skip to content\n" +
                " \n" +
                "Search or jump to…\n" +
                "\n" +
                "Pull requests\n" +
                "Issues\n" +
                "Marketplace\n" +
                "Explore\n" +
                " \n" +
                "@AlexLiuSheng \n" +
                "Repositories\n" +
                "Find a repository…\n" +
                "AlexLiuSheng\n" +
                "/\n" +
                "EasyBluetoothFrame\n" +
                "AlexLiuSheng\n" +
                "/\n" +
                "CheckVersionLib\n" +
                "Qihoo360\n" +
                "/\n" +
                "RePlugin\n" +
                "AlexLiuSheng\n" +
                "/\n" +
                "AlexLiuSheng.github.io\n" +
                "AlexLiuSheng\n" +
                "/\n" +
                "pandora\n" +
                "AlexLiuSheng\n" +
                "/\n" +
                "AndroidUtilCode\n" +
                "AlexLiuSheng\n" +
                "/\n" +
                "pandora-no-op\n" +
                "Your teams\n" +
                "Find a team…\n" +
                "otzkxy/owners\n" +
                "Dashboard\n" +
                "@Trinea\n" +
                "Trinea starred hope-for/hope-boot 4 hours ago\n" +
                "hope-for/hope-boot\n" +
                "\uD83C\uDF31\uD83D\uDE80一款现代化的脚手架项目。企业开发？接外包？赚外快？还是学习？这都能满足你，居家必备，值得拥有\uD83C\uDF7B整合Springboot2，单点登陆+tk.mybatis+shiro+redis+thymeleaf+maven+swagger前后端分离接口管理+代码生成+定时任务+数据库版本管理flywa…\n" +
                "\n" +
                " Java  1.8k  Updated Aug 26\n" +
                "\n" +
                "@471448446\n" +
                "471448446 starred 2 repositories  23 hours ago\n" +
                "WangDaYeeeeee/Mysplash\n" +
                "An Unsplash Client\n" +
                "\n" +
                " Java  598  Updated Aug 25\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred Zzzia/EasyBook 2 days ago\n" +
                "Zzzia/EasyBook\n" +
                "Java/Android多站点小说爬虫，制作成工具类，并发搜索下载，支持追更\n" +
                "\n" +
                " Java  112  Updated Aug 26\n" +
                "\n" +
                "@HeyjokingChan\n" +
                "HeyjokingChan starred AlexLiuSheng/EasyBluetoothFrame 2 days ago\n" +
                "AlexLiuSheng/EasyBluetoothFrame\n" +
                "经典（传统）蓝牙快速开发框架，A fast develop frame of classic bluetooth\n" +
                "\n" +
                " Java  43  Updated Aug 26\n" +
                "\n" +
                "@HeyjokingChan\n" +
                "HeyjokingChan forked HeyjokingChan/EasyBluetoothFrame from AlexLiuSheng/EasyBluetoothFrame 2 days ago\n" +
                "AlexLiuSheng/EasyBluetoothFrame\n" +
                "经典（传统）蓝牙快速开发框架，A fast develop frame of classic bluetooth\n" +
                "\n" +
                " Java  43  Updated Aug 26\n" +
                "\n" +
                "@hongyangAndroid\n" +
                "hongyangAndroid starred 2 repositories  2 days ago\n" +
                "Sky24n/flutter_wanandroid\n" +
                "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25 基于Google Flutter的WanAndroid客户端，支持Android和iOS。包括BLoC、RxDart 、国际化、主题色、启动页、引导页，拥有较好的项目结构&比较规范的代码！\n" +
                "\n" +
                " Dart  2.9k  Updated Aug 26\n" +
                "\n" +
                "@hongyangAndroid\n" +
                "hongyangAndroid starred phoenixsky/fun_android_flutter 2 days ago\n" +
                "phoenixsky/fun_android_flutter\n" +
                "产品级Flutter开源项目FunAndroid，Provider的最佳实践\n" +
                "\n" +
                " Dart  197  Updated Aug 26\n" +
                "\n" +
                "@RaphetS\n" +
                "RaphetS starred AlexLiuSheng/CheckVersionLib 2 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "@higsx\n" +
                "higsx starred AlexLiuSheng/CheckVersionLib 3 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "@Cansll\n" +
                "Cansll starred AlexLiuSheng/CheckVersionLib 4 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "@canzu\n" +
                "canzu starred AlexLiuSheng/EasyBluetoothFrame 4 days ago\n" +
                "AlexLiuSheng/EasyBluetoothFrame\n" +
                "经典（传统）蓝牙快速开发框架，A fast develop frame of classic bluetooth\n" +
                "\n" +
                " Java  43  Updated Aug 26\n" +
                "\n" +
                "@hongyangAndroid\n" +
                "hongyangAndroid starred hongyangAndroid/AndroidAutoLayout 4 days ago\n" +
                "hongyangAndroid/AndroidAutoLayout\n" +
                "[停止维护]Android屏幕适配方案，直接填写设计图上的像素尺寸即可完成适配，最大限度解决适配问题。\n" +
                "\n" +
                " Java  6.7k  Updated Aug 26\n" +
                "\n" +
                "@it212\n" +
                "it212 forked it212/FloatView from AlexLiuSheng/FloatView 4 days ago\n" +
                "AlexLiuSheng/FloatView\n" +
                "floatview on windowmanager,and you can be easy to add call show by using it\n" +
                "\n" +
                " Java  231  Updated Aug 2\n" +
                "\n" +
                "@qiwei0727\n" +
                "qiwei0727 starred AlexLiuSheng/CheckVersionLib 4 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "@KevenT\n" +
                "KevenT starred AlexLiuSheng/CircleMenuView 4 days ago\n" +
                "AlexLiuSheng/CircleMenuView\n" +
                "CircleMenuView that have many custom functions\n" +
                "\n" +
                " Java  82  Updated Aug 22\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred wireapp/wire-android 5 days ago\n" +
                "wireapp/wire-android\n" +
                "☎️ Wire for Android\n" +
                "\n" +
                " Scala  2.3k  18 issues need help Updated Aug 26\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred mxdldev/spring-cloud-flycloud 5 days ago\n" +
                "mxdldev/spring-cloud-flycloud\n" +
                "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25FlyClould 微服务实战项目框架，在该框架中，包括了用 Spring Cloud 构建微服务的一系列基本组件和框架，对于后台服务框架的搭建有很大的参考价值，大家可以参考甚至稍加修改可以直接应用于自己的实际的项目开发中，该项目没有采用Maven进行项目构建，Maven通过xml进行…\n" +
                "\n" +
                " Java  1.2k  Updated Aug 26\n" +
                "\n" +
                "@471448446\n" +
                "471448446 starred googlesamples/android-architecture-components 5 days ago\n" +
                "googlesamples/android-architecture-components\n" +
                "Samples for Android Architecture Components.\n" +
                "\n" +
                " Kotlin  14.3k  Updated Aug 26\n" +
                "\n" +
                "@liuxiaodongdefault\n" +
                "liuxiaodongdefault started following you 5 days ago\n" +
                "liuxiaodongdefault\n" +
                "liuxiaodong liuxiaodongdefault\n" +
                "Valar Morghulis\n" +
                "126 repositories 11 followers\n" +
                "\n" +
                "@liuxiaodongdefault\n" +
                "liuxiaodongdefault forked liuxiaodongdefault/CircleMenuView from AlexLiuSheng/CircleMenuView 5 days ago\n" +
                "AlexLiuSheng/CircleMenuView\n" +
                "CircleMenuView that have many custom functions\n" +
                "\n" +
                " Java  82  Updated Aug 22\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred lopspower/GradientView 5 days ago\n" +
                "lopspower/GradientView\n" +
                "Create gradient view in Android in the simplest way possible \uD83C\uDF08\n" +
                "\n" +
                " Kotlin  104  Updated Aug 26\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred 3 repositories  5 days ago\n" +
                "siacs/Conversations\n" +
                "Conversations is an open source XMPP/Jabber client for the Android platform\n" +
                "\n" +
                " Java  3.3k  Updated Aug 26\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred 2 repositories  6 days ago\n" +
                "quickhybrid/quickhybrid\n" +
                "快速混合开发框架，JSBridge提供核心Android、iOS功能。多平台API支撑，部分兼容浏览器\n" +
                "\n" +
                " JavaScript  310  Updated Aug 26\n" +
                "\n" +
                "@whataa\n" +
                "whataa started following enbandari 6 days ago\n" +
                "enbandari\n" +
                "Bennyhuo enbandari\n" +
                "微信公众号 Bennyhuo\n" +
                "172 repositories 939 followers\n" +
                "\n" +
                "@hideuvpn\n" +
                "hideuvpn starred AlexLiuSheng/CheckVersionLib 6 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "@Trinea\n" +
                "Trinea starred 3 repositories  6 days ago\n" +
                "Dawish/MDPlayer\n" +
                "\uD83D\uDD25 MDPlayer，Android万能播放器，支持视频大小窗口无缝切换，基于ijklayer+MVP+RxJava+Retrofit+Material Design开发。\n" +
                "\n" +
                " Java  410  Updated Aug 23\n" +
                "\n" +
                "@hongyangAndroid\n" +
                "hongyangAndroid starred LinHuanTanLy/FlutterWanAndroid 6 days ago\n" +
                "LinHuanTanLy/FlutterWanAndroid\n" +
                "垃圾肇聪 让爸爸给你写个demo\n" +
                "\n" +
                " Dart  9  Updated Aug 25\n" +
                "\n" +
                "@cjlsuper\n" +
                "cjlsuper, XunGuo, and damoncat starred 1 repository  6 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "@tianshaokai\n" +
                "tianshaokai starred AlexLiuSheng/CircleMenuView 6 days ago\n" +
                "AlexLiuSheng/CircleMenuView\n" +
                "CircleMenuView that have many custom functions\n" +
                "\n" +
                " Java  82  Updated Aug 22\n" +
                "\n" +
                "@XunGuo\n" +
                "XunGuo forked XunGuo/CheckVersionLib from AlexLiuSheng/CheckVersionLib 6 days ago\n" +
                "AlexLiuSheng/CheckVersionLib\n" +
                "版本检测升级（更新）库。an auto check version library（app update） on Android\n" +
                "\n" +
                " Java  2k  Updated Aug 24\n" +
                "\n" +
                "More\n" +
                " ProTip! The feed shows you events from people you follow and repositories you watch. \n" +
                "Subscribe to your news feed\n" +
                "© 2019 GitHub, Inc.\n" +
                "Blog\n" +
                "About\n" +
                "Shop\n" +
                "Contact GitHub\n" +
                "Pricing\n" +
                "API\n" +
                "Training\n" +
                "Status\n" +
                "Security\n" +
                "Terms\n" +
                "Privacy\n" +
                "Help\n" +
                "GitHub Actions beta, now with CI/CD\n" +
                "Automate any workflow with GitHub Actions. See the latest updates and register for the beta.\n" +
                "Explore repositories\n" +
                "KhronosGroup/glTF\n" +
                "glTF – Runtime 3D Asset Delivery\n" +
                "\n" +
                " HTML  3.3k\n" +
                "GameFoundry/bsf\n" +
                "Modern C++14 library for the development of real-time graphical applications\n" +
                "\n" +
                " C++  1.4k\n" +
                "MrCodeSniper/PopLayer\n" +
                "通用Android端弹窗管理框架,支持带网络请求的业务流程管理,内部维护弹窗优先级队列 具备弹窗管理扩展功能 整合Dialog,PoupoWindow,悬浮Widget,透明Webview,Toast,SnackBar,无需再为繁琐的业务弹窗逻辑所困扰\n" +
                "\n" +
                " Java  237\n" +
                "Explore more →\n" +
                "Team of 5? Get Pro discount\n"
        val bytes = text.toByteArray()
        val b = ByteBuffer.allocate(5 + bytes.size)
        b.order(ByteOrder.LITTLE_ENDIAN)
        b.putInt(-0x146f1470)
        b.put(bytes)
        b.put(0x03)

        connect?.write(b.array(), object : TransferProgressListener {
//            override fun disconnected() {
//                t("bluetooth has disconnected")
//
//            }

            override fun transferSuccess(bytes: ByteArray?) {
                t("send message successful")
            }

            override fun transferFailed(msg: Exception) {
                msg.message?.run {
                    t(this)
                }

            }

            override fun transfering(progress: Int) {
                CLog.e("write progress:$progress")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().destory()
    }
}
