package kr.poturns.virtualpalace.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Myungjin Kim on 2015-09-08.
 */
public class DriveRestAssistant {
    /**
     * 최상위 폴더의 ID
     */
    public static final String FOLDER_ID_ROOT = "root";
    private static final String FOLDER_MIME = "application/vnd.google-apps.folder";

    private Drive mDriveService;
    private Drive.Parents mParentsMethodSet;
    private Drive.Children mChildrenMethodSet;
    private Drive.Files mFilesMethodSet;

    public DriveRestAssistant(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] googleAccounts = accountManager.getAccountsByType("com.google");

        mDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                AndroidJsonFactory.getDefaultInstance(),
                GoogleAccountCredential.usingOAuth2(context, Collections.singletonList(DriveScopes.DRIVE_READONLY))
                        .setSelectedAccountName(googleAccounts[0].name))
                .setApplicationName(context.getApplicationInfo().name)
                .build();

        mParentsMethodSet = mDriveService.parents();
        mChildrenMethodSet = mDriveService.children();
        mFilesMethodSet = mDriveService.files();
    }

    /**
     * Drive와의 연결을 종료하고, 자원을 반납한다.
     */
    public void destroy() {
        mParentsMethodSet = null;
        mChildrenMethodSet = null;
        mFilesMethodSet = null;
        mDriveService = null;
    }

    /**
     * 주어진 폴더에 속한 파일 또는 폴더의 리스트를 검색한다.
     *
     * @param folderId 검색할 폴더의 ID
     * @return 주어진 폴더에 속한 파일 또는 폴더의 리스트
     */
    public List<File> requestChildFileList(String folderId) throws IOException {
        List<ChildReference> childReferences = requestChildReferenceList(folderId);

        BatchRequest batchRequest = mDriveService.batch();
        final int N = childReferences.size();
        final List<File> fileList = new ArrayList<File>(N);
        for (int i = 0; i < N; i++) {
            ChildReference childReference = childReferences.get(i);

            batchRequest.queue(configureRequest(partialRequest(mFilesMethodSet.get(childReference.getId())).buildHttpRequest()),
                    File.class, Void.class, new BatchCallback<File, Void>() {
                        @Override
                        public void onSuccess(File file, HttpHeaders responseHeaders) throws IOException {
                            if (isValidFile(file)) {
                                synchronized (fileList) {
                                    fileList.add(file);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Void aVoid, HttpHeaders responseHeaders) throws IOException {
                        }
                    });
        }
        batchRequest.execute();

        Collections.sort(fileList, FILE_COMPARATOR);

        return fileList;
    }

    /**
     * 파일 정렬 {@link Comparator}, 폴더 / 이름 순으로 우선순위
     */
    private static final Comparator<File> FILE_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            String lTitle = lhs.getTitle(), rTitle = rhs.getTitle();
            boolean lIsFolder = isFolder(lhs), rIsFolder = isFolder(rhs);

            if (lIsFolder) {
                // 모두 폴더 - 이름 비교
                if (rIsFolder) return lTitle.compareTo(rTitle);
                    // l - 폴더
                else return -1;
            } else {
                // 모두 폴더 아님 - 이름 비교
                if (!rIsFolder) return lTitle.compareTo(rTitle);
                    // r - 폴더
                else return 1;
            }
        }
    };

    public List<File> requestParentList(List<ParentReference> parentReferences) throws IOException {
        BatchRequest batchRequest = mDriveService.batch();
        final int N = parentReferences.size();
        final List<File> fileList = new ArrayList<File>(N);
        for (int i = 0; i < N; i++) {
            ParentReference parentReference = parentReferences.get(i);

            batchRequest.queue(configureRequest(partialRequest(mFilesMethodSet.get(parentReference.getId())).buildHttpRequest()),
                    File.class, Void.class, new BatchCallback<File, Void>() {
                        @Override
                        public void onSuccess(File file, HttpHeaders responseHeaders) throws IOException {
                            synchronized (fileList) {
                                fileList.add(file);
                            }
                        }

                        @Override
                        public void onFailure(Void aVoid, HttpHeaders responseHeaders) throws IOException {
                        }
                    });
        }
        batchRequest.execute();

        Collections.sort(fileList, FILE_COMPARATOR);

        return fileList;
    }

    /**
     * 주어진 파일이 속한 폴더를 검색한다.
     *
     * @param fileId 검색할 파일의 ID
     * @return 주어진 파일이 속한 폴더의 리스트
     */
    public List<File> requestParentList(String fileId) throws IOException {
        return requestParentList(requestParentReferenceList(fileId));
    }

    /**
     * 주어진 파일의 상위폴더의 메타데이터 리스트를 가져온다
     *
     * @param fileId 검색할 파일의 id
     */
    public List<ParentReference> requestParentReferenceList(String fileId) throws IOException {
        return mParentsMethodSet.list(fileId).execute().getItems();
    }


    /**
     * 주어진 폴더에 속한 파일들의 메타데이터 리스트를 가져온다
     *
     * @param folderId 검색할 폴더의 id
     */
    private List<ChildReference> requestChildReferenceList(String folderId) throws IOException {
        List<ChildReference> list = new ArrayList<ChildReference>();

        Drive.Children.List request = mChildrenMethodSet.list(folderId);
        do {
            try {
                ChildList children = request.setFields("nextPageToken,items(id)").execute();

                list.addAll(children.getItems());
                request.setPageToken(children.getNextPageToken());
            } catch (IOException e) {
                e.printStackTrace();
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null && request.getPageToken().length() > 0);

        return list;
    }

    public File getFile(String fileId) throws IOException {
        return partialRequest(mFilesMethodSet.get(fileId)).execute();
    }
/*
    public byte[] openFile(String fileId) throws IOException {
        return openFile(mDriveService.files().get(fileId).setFields("downloadUrl").execute());
    }
*/

    /**
     * 주어진 파일을 열어, 그 내용을 반환한다.
     *
     * @param file 내용을 볼 파일
     * @return 파일의 내용이 기록된 바이트 배열
     */
    public InputStream openFile(File file) throws IOException {
        if (file.getDownloadUrl() == null || file.getDownloadUrl().length() < 1)
            return null;

        return mDriveService.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute().getContent();
    }

    /**
     * 파일 메타데이터 요청시, 모든 데이터가 아닌 일부만 요청한다.
     */
    private static Drive.Files.Get partialRequest(Drive.Files.Get request) {
        return request.setFields("title,mimeType,description,id,downloadUrl,explicitlyTrashed,selfLink,iconLink"); // string
        //.setFields("createdDate") // dateTime
        //.setFields("version"); // long
    }

    /**
     * 파일 메타데이터 요청시, Http 요청의 내용을 일부 변경하여 최적화 한다.
     */
    private static HttpRequest configureRequest(HttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        httpHeaders.setAcceptEncoding("gzip");
        httpHeaders.setUserAgent(httpHeaders.getUserAgent().concat(" (gzip)"));
        //request.setConnectTimeout(30000);
        //request.setReadTimeout(30000);
        return request;
    }

    /**
     * 유효한 파일인지 확인한다.
     */
    private static boolean isValidFile(File file) {
        return file != null && file.getTitle() != null && !file.getExplicitlyTrashed() && !file.getTitle().startsWith("~$") && !file.getTitle().endsWith("[충돌]");
    }

    /**
     * 주어진 {@link File}이 폴더 여부를 확인한다.
     */
    public static boolean isFolder(File file) {
        return file.getMimeType().equals(FOLDER_MIME);
    }

}

