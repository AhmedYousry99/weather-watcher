package com.senseicoder.productsworkmanager.workers

/*
class MyWorker(private val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val data = Data.Builder()
        val res = ProductRemoteDataSourceImpl.getAllProducts().execute()
        if (res.isSuccessful) {
            Log.d("TAG", "doWork: ${res.body()}")

            data.putString("product", Gson().toJson(res.body()))

            return Result.success(data.build())
        } else
            return Result.failure()
    }
}*/
