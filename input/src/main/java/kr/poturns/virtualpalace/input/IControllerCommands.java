package kr.poturns.virtualpalace.input;

/**
 *
 * @author Yeonho.Kim
 */
public interface IControllerCommands {

    // * * * S U P P O R T  F L A G S * * * //
    /**
     *
     */
    int TYPE_INPUT_SUPPORT_SCREENTOUCH = 0x1;
    /**
     *
     */
    int TYPE_INPUT_SUPPORT_SCREENFOCUS = 0x2;
    /**
     *
     */
    int TYPE_INPUT_SUPPORT_MOTION = 0x4;
    /**
     *
     */
    int TYPE_INPUT_SUPPORT_VOICE = 0x10;
    /**
     *
     */
    int TYPE_INPUT_SUPPORT_WATCH = 0x20;



    // * * * C O M M A N D S * * * //
    /**
     *
     */
    int INPUT_SYNC_COMMAND = 0x10;
    /**
     *
     */
    int INPUT_SINGLE_COMMAND = 0x11;
    /**
     *
     */
    int INPUT_MULTI_COMMANDS = 0x12;


    /**
     *
     */
    int REQUEST_MESSAGE = 0x10;
    /**
     *
     */
    int REQUEST_MESSAGE_FROM_UNITY = 0x10;
    /**
     *
     */
    int REQUEST_CALLBACK_FROM_UNITY = 0x11;



    // * * * J S O N  C O M M A N D S * * * //
    /**
     * 명령 기본 포맷 : JSON Object
        {
            COMMAND : {
                CONTENTS
            },

            [COMMAND OPTION : {
                CONTENTS
            }]*,

            (response) RESULT : SUCCESS / FAIL / ERROR
        }
     */
    /*
     * AR/VR/RES 데이터 삽입/수정/삭제/조회
        {
            INSERT_VR/AR/RES : {
                SET : {
                 //  삽입할 필드 - 값

                }
            }

            UPDATE_VR/AR/RES : {
                SET : {
                    //  수정할 필드 - 값

                },
                WHERE : {
                    // 수정할 데이터의 조건 : EQUAL : 필드 - 값

                }
                WHERE_NOT : {
                    // 수정할 데이터의 조건 : NOT_EQUAL : 필드 - 값

                }
                WHERE_GREATER : {
                    // GREATER : 필드 - 값

                    ALLOW_EQUAL : true / false
                }
                WHERE_SMALLER : {
                    // SMALLER : 필드 - 값

                    ALLOW_EQUAL : true / false
                }
                WHERE_FROM : {
                    // 수정할 데이터의 조건 : BETWEEN, FROM : 필드 - 값
                    // 반드시 WHERE_TO 와 함께 기술되어야 함.

                 }
                 WHERE_TO : {
                    // 수정할 데이터의 조건 : BETWEEN, TO : 필드 - 값
                    // 반드시 WHERE_FROM 과 함께 기술되어야 함.

                 }
                 WHERE_LIKE : {
                    // 수정할 데이터의 조건 : LIKE : 필드 - 값

                 }
            }

            DELETE_VR/AR/RES : {
                // 삭제할 데이터의 조건 : 필드 - 값
                WHERE : {
                    // EQUAL : 필드 - 값

                }
                WHERE_NOT : {
                    // NOT_EQUAL : 필드 - 값

                }
                WHERE_GREATER : {
                    // GREATER : 필드 - 값

                    ALLOW_EQUAL : true / false
                }
                WHERE_SMALLER : {
                    // SMALLER : 필드 - 값

                    ALLOW_EQUAL : true / false
                }
                WHERE_FROM : {
                    // BETWEEN, FROM : 필드 - 값
                    // 반드시 WHERE_TO 와 함께 기술되어야 함.

                 }
                 WHERE_TO : {
                    // BETWEEN, TO : 필드 - 값
                    // 반드시 WHERE_FROM 과 함께 기술되어야 함.

                 }
                 WHERE_LIKE : {
                    // LIKE : 필드 - 값

                 }

            }

        }
     */
    /*
     * 상태 알림
        {
            SWITCH_PLAY_MODE : < PLAY MODE VALUE : 양수일 경우 Cardboard On / 음수일 경우 OFF>

            ACTIVATE_INPUT : < INPUT MODULE VALUE >

            DEACTIVATE_INPUT : < INPUT MODULE VALUE >


        }
     */




    // * * * J S O N  C O M M A N D S  K E Y S * * * //
    interface JsonKey {
        /**
         * (응답시) 결과 상태 KEY.
         */
        String RESULT = "result";

        String INSERT_VR = "insert_vr";
        String INSERT_AR = "insert_ar";
        String INSERT_RES = "insert_res";

        String UPDATE_VR = "update_vr";
        String UPDATE_AR = "update_ar";
        String UPDATE_RES = "update_res";

        String DELETE_VR = "delete_vr";
        String DELETE_AR = "delete_ar";
        String DELETE_RES = "delete_res";

        String SWITCH_PLAY_MODE = "switch_play_mode";
        String ACTIVATE_INPUT = "activate_input";
        String DEACTIVATE_INPUT = "deactivate_input";

        /**
         *
         */
        String SET = "set";
        /**
         *
         */
        String ALLOW_EQUAL = "allow_equal";

        String WHERE = "where";

        String WHERE_NOT = "where_not";

        String WHERE_GREATER = "where_greater";

        String WHERE_SMALLER = "where_smaller";

        String WHERE_FROM = "where_from";

        String WHERE_TO = "where_to";

        String WHERE_LIKE = "where_like";

    }
}
