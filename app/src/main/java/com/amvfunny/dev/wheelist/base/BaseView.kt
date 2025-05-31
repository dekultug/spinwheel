package com.amvfunny.dev.wheelist.base

interface BaseView {

    /**
     * Cấu hình activity/fragment/dialog
     */
//    fun getConfig(): T

    /**
     * Gọi trước khi khởi tạo view
     */
    fun onPrepareInitView() {}

    /**
     * Gọi khi gán instance cho data binding
     */
    fun onInitBinding() {}

    /**
     * Gọi sau khi đã khởi tạo view
     */
    fun onInitView()

    /**
     * Gọi sau khi view được khởi tạo, quan sát dữ liệu
     */
    fun onObserverViewModel() {}

    /**
     * Gọi khi destroy view (khi sử dụng replace fragment) để tránh gây ra leak nếu như khởi tạo
     * các đối tượng từ onViewCreated() trở đi
     */
    fun onCleaned() {}
}
