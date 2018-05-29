Parameter
ExtendedBluetoothDevice extendedBluetoothDevice   蓝牙设备对象(不方便的话,目前传null也可以)   Bluetooth Device Object
int uid 用户账号id(服务端获取) 即openid Equate to openid
String lockVersion                               锁版本信息(json格式,跟上传到服务器格式一致)   Lock Version information
String adminPs 判断管理员(添加管理员之后会返回,原样传输就行,锁内使用) Determine whether it is an administrator
String unlockKey                                 开门Key(添加管理员之后会返回,原样传输就行,锁内使用)  unlock needed
int lockFlagPos 锁标志位用于重置电子钥匙(初始化值为0,每重置一次+1) The Flag used to reset Ekey(init 0)
String aesKeyStr 加密Key(添加管理员之后会返回,原样传输就行,锁内使用) AES Key
long unlockDate 开锁时间(时间戳 long类型 三代锁用于校准锁时间,传入0或者小于0表示不进行校准操作) Unlock Date
long startDate 开始时间(时间戳 long类型) Start Date
long endDate 结束时间(时间戳 long类型) End Date
long timezoneOffset (传入-1不考虑偏移量，使用默认值)锁时区和UTC时区时间的差数，单位milliseconds(以服务端返回的为准)

API
public boolean isBLEEnabled(Context context)
用于判断蓝牙是否打开                          Determine if Bluetooth is turn on

public void requestBleEnable(Activity activity)
请求打开蓝牙                                 Request to turn on Bluetooth

public void startBleService(Context context)
启动蓝牙服务 使用蓝牙接口必须要启动蓝牙服务     Start Bluetooth Service

public void stopBleService(Context context)
关闭蓝牙服务   Stop Bluetooth Service

public void startBTDeviceScan()
启动蓝牙扫描 Start Bluetooth Scan

public void stopBTDeviceScan()
停止扫描 Stop Bluetooth Scan

public void connect(String address)
通过mac地址连接蓝牙设备 Connect Device by mac address

public void connect(ExtendedBluetoothDevice device)
通过设备对象连接蓝牙设备(推荐使用这种方式连接) Connect Device by ExtendedBluetoothDevice object

public void disconnect()
断开蓝牙连接 Disconnect Bluetooth

public void addAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice)
添加管理员 Add Administrator

public void resetLock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
恢复出厂设置 管理员删除锁的时候 也要调用该指令删除锁内管理员 否则无法重新添加 Reset Lock

public void unlockByAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long unlockDate, String aesKeyStr, long timezoneOffset)
管理员开门(车位锁降)             Admin Unlock(Pad Lock Down)

public void lockByAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
管理员 车位锁升起 Pad Lock Up

public void unlockByUser(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset)
电子钥匙开门(车位锁降) Ekey Unlock(Pad Lock Down)

public void lockByUser(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset)
普通用户 车位锁升起               Pad Lock Up

public void lock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, long lockDate, String aesKeyStr, long timezoneOffset)
门锁的闭锁指令

public void setLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String unlockKey, long date, int lockFlagPos, String aesKeyStr, long timezoneOffset)
校准时间 Set Lock Time
date 需要校准的时间(传入时间毫秒数)

public void resetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
密码初始化 Init Passwords

public void resetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, @NonNull String adminPs, int lockFlagPos, String aesKeyStr)
重置电子钥匙 Reset EKey

public void getOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset)
读取操作日志(包括车位锁的警报记录)   Read Lock Log

public void addPeriodKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String password, long startDate, long endDate, String aesKeyStr, long timezoneOffset)
添加期限密码 自定义密码 Add Password

public void deleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, @NonNull String password, String aesKeyStr)
删除单个键盘密码 Delete Password

public void modifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, String originalPwd, String newPwd, long startDate, long endDate, String aesKeyStr, long timezoneOffset)
修改键盘密码 Modify Password
keyboardPwdType 键盘密码类型(目前不要求，可以传0)
originalPwd 原始密码
newPwd 新密码(传null或者空字符串表示不修改密码)
startDate 开始时间(时间戳 long类型 传<=0表示不修改时间)
endDate 结束时间(时间戳 long类型 传<=0表示不修改时间)

public void getLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset)
获取锁时间      Read Lock Time

public void searchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
获取设备特征值(用于判断所支持的设备) Search Device Feature

public void addICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
添加IC卡 Add IC

public void searchICCardNumber(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
查询IC卡号 Search IC No.

public void modifyICPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, long startDate, long endDate, String aesKeyStr, long timezoneOffset)
修改IC卡有效期 Modify IC Period

public void deleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, String aesKeyStr)
删除IC卡 Delete IC

public void clearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
清空IC卡 Clear IC

public void addFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
添加指纹           Add FingerPrint

public void modifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long FRNo, long startDate, long endDate, String aesKeyStr, long timezoneOffset)
修改指纹效期 Modify FingerPrint Period

public void deleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, String aesKeyStr)
删除指纹 Delete FingerPrint

public void clearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr)
清空指纹 Clear FingerPrint

public void readDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr)
获取设备信息 Read Device Information

public void setAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, String password)
设置管理员键盘密码 Set Admin Keyboard Password

TTLockCallback
onFoundDevice(final ExtendedBluetoothDevice extendedBluetoothDevice)
发现设备回调 Found Device

onDeviceConnected(final ExtendedBluetoothDevice extendedBluetoothDevice)
连接上蓝牙 Device Connected

onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice)
蓝牙断开 Device Disconnected

onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int protocolType, int protocolVersion, int scene, int groupId, int orgId, Error error)
获取版本信息       Read Lock Version
extendedBluetoothDevice 蓝牙设备对象
protocolType 协议类型
protocolVersion 协议版本
scene 场景
groupId 组ID
orgId 子组ID

onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error)
adminKeyboardPwd 管理码
deletePwd 清空码
pwdInfo 密码数据
timestamp 时间戳
aesKeystr 加密Key,使用时按照原数据传入接口即可
feature 设备特征
modelNumber 型号
hardwareRevision 硬件版本号
firmwareRevision 固件版本号

onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int lockFlagPos, Error error)
重置电子钥匙回调 Reset EKey

onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminKeyboardPwd, Error error)
设置管理员键盘密码回调 Set Supper Password

onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error)
开锁回调(车位锁降下) Unlock
uniqueid 开锁的唯一标识id 只有三代锁有用 其它默认(取系统时间)
lockTime 锁时间 只有三代锁有用 其它默认(取系统时间)

onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int uid, int uniqueid, long lockTime, Error error)
闭锁回调(车位锁升起)

onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) 校准时间成功回调 Set Lock Time

onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long lockTime, Error error) 获取锁时间 Read Lock Time

onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String pwdInfo, long timestamp, Error error)
键盘密码初始化 Reset Keyboard Password

onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error)
恢复出厂设置 Reset Lock

onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String password, long startDate, long endDate, Error error)
添加键盘密码回调 Add Supper Password
keyboardPwdType 2-永久 1-单次 3-期限(同服务端一致)
password 添加的密码
startDate 密码使用起始时间
endDate 密码使用截止时间(永久密码类型此参数无意义)

onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String originPwd, String newPwd, Error error)
修改密码 Modify Keyboard Password
keyboardPwdType 密码类型
originPwd 原始密码
newPwd 修改后密码

onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String deletedPwd, Error error)
删除单个密码回调 Delete Keyboard Password

onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error)
删除所有键盘密码回调 Delete All Keyboard Password

onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String records, Error error)
获取操作日志(包含车位锁的警报记录) Read Lock Log
records 日志记录(json格式，传入服务端即可)

onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int feature, Error error)
查询设备特征               Search Device Feature
feature                   锁特征值(用于判断锁支持的功能)

onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long cardNo, Error error)
添加IC卡           Add IC
cardNo                       IC卡卡号
status 1 - 进入添加模式 2 - 添加成功

onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, long startDate, long endDate, Error error)
修改IC卡有效期       Modify IC Period

onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, Error error)
删除IC卡 Delete IC

onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error)
清空IC卡 Clear IC

onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, Error error)
添加指纹回调 Add IC

onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error)
指纹采集 Collecte FingerPrint

onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, long startDate, long endDate, Error error)
修改指纹有效期 Modify FingerPrint Period

onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, Error error)
删除指纹 Delete FingerPrint

onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error)
清空指纹           Clear FingerPrint

onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String modelNumber, String hardwareRevision, String firmwareRevision, String manufactureDate, String lockClock)
读取设备信息 Read Device Information
modelNumber 产品型号("M201")
hardwareRevision 硬件版本号("1.3")
firmwareRevision 固件版本号("2.1.16.705")
manufactureDate 生产日期("20160707")
lockClock 时钟("170105153105") 年月日时分秒

DeviceFirmwareUpdateApi
Status 升级过程中的状态
public static final int UpgradeOprationPreparing = 1; //准备中 Preparing
public static final int UpgradeOprationUpgrading = 2; //升级中 Upgrading
public static final int UpgradeOprationRecovering = 3; //恢复中 Recovering
public static final int UpgradeOprationSuccess = 4; //升级成功 Upgrade successed

Error  升级过程的错误码
public static final int DfuFailed = 1; //固件升级失败 Upgrade Failed
public static final int BLEDisconnected = 2;                       //蓝牙断开     Bluetooth disconnected
public static final int BLECommandError = 3; //蓝牙指令错误 Command Error
public static final int RequestError = 4; //服务器请求错误 Request Error
public static final int NetError = 5; //网络错误 Net Error

DeviceFirmwareUpdateCallback
onGetLockFirmware(int specialValue, String module, String hardware, String firmware); 获取设备固件信息 Read Lock Firmware
onStatusChanged(int status) 升级状态改变回调 Status Changed
onDfuProcessStarting(final String deviceAddress) DFU启动 Device Firmware Upgrade Start
onEnablingDfuMode(final String deviceAddress) 进入DFU模式     Enter Device Firmware Upgrade Mode
onDfuCompleted(final String deviceAddress) 固件升级过程完成 Device Firmware Upgrade Completed
onDfuAborted(final String deviceAddress) 固件升级过程中断 Device Firmware Upgrade Aborted
onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal)
固件升级过程中的进度 Progress Changed
onError(int errorCode, Error error, String errorContent)
错误回调 Error errorCode       错误码
error           蓝牙错误
errorContent    服务器错误