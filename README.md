# Dozip
Android WebView 本地更新-ZIP文件下载，解压，加载等。

    /**
     * 下载
     */
    private void doDownLoadWork() {
        DownLoaderTask task = new DownLoaderTask("http://www.html5code.net/uploads/soft/160407/1-16040H21141.zip",
                PATH, this, mZipOverListener);
        //DownLoaderTask task = new DownLoaderTask("http://192.168.9.155/johnny/test.h264", getCacheDir().getAbsolutePath()+"/", this);
        task.execute();
    } 
    /**
     * 将资源文件打包到Assets中，第一次加载时 拷贝到SD
     * @param strOutFileName
     */
    private void copyBigDataToSD(String strOutFileName) {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myOutput = new FileOutputStream(strOutFileName);
            myInput = this.getAssets().open("1-16040H21141.zip");
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
        } finally {
            ZipExtractorTask task = new ZipExtractorTask(PATH + "/" + "1-16040H21141.zip", PATH, MainActivity.this, true,
                    null);
            task.execute();
        }
    }
