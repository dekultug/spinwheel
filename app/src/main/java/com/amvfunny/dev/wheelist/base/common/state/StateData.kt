package com.amvfunny.dev.wheelist.base.common.state

sealed class StateData<Data>(

    var data: Data? = null,

    var status: STATE_TYPE,

    var exception: Throwable? = null,
) {

    class Init<Data> : StateData<Data>(null, STATE_TYPE.INIT)

    class Error<Data>(
        data: Data? = null,
        throwable: Throwable
    ) : StateData<Data>(
        data = data,
        status = STATE_TYPE.ERROR,
        exception = throwable
    )

    class Success<Data>(data: Data?) : StateData<Data>(data = data, status = STATE_TYPE.SUCCESS)

}
