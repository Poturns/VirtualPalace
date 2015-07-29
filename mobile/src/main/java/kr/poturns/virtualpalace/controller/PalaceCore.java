package kr.poturns.virtualpalace.controller;

import kr.poturns.virtualpalace.db.LocalArchive;

/**
 * 컨트롤러의 내부적 기능을 정의한다.
 *
 * Created by YeonhoKim on 2015-07-29.
 */
class PalaceCore {

    protected final PalaceApplication mContextF;
    protected  final LocalArchive mLocalArchiveF;

    protected PalaceCore(PalaceApplication context) {
        mContextF = context;
        mLocalArchiveF = LocalArchive.getInstance(context);
    }




}
