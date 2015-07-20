package kr.poturns.virtualpalace.input;

/**
 * Created by YeonhoKim on 2015-07-06.
 */
public interface IOperationInputFilter<InputUnit> {

    /**
     * ���� ���
     */
    public enum Direction {
        // ��(��) ����
        NONE,

        // 3D ����
        UPWARD,     // ��
        DOWNWARD,   // �Ʒ�
        FORWARD,    // ��
        BACKWARD,   // ��

        // 2D ����
        EAST,       // ��
        WEST,       // ��
        SOUTH,      // ��
        NORTH,      // ��
        N_E,        // �ϵ�
        N_W,        // �ϼ�
        S_E,        // ����
        S_W,        // ����
        CENTER      // �߾�
    }

    /**
     * ��ɾ� ���
     */
    public enum Operation {
        GO,
        TURN,
        FOCUS,
        ZOOM,
        SELECT
    }

    /**
     * Return �������� �̵��Ѵ�.
     * @param unit
     * @return
     */
    Direction isGoingTo(InputUnit unit);

    /**
     * Return �������� ȸ���Ѵ�.
     * @param unit
     * @return
     */
    Direction isTurningTo(InputUnit unit);

    /**
     * Return �������� ��Ŀ��(Focusing)�Ѵ�.
     * @param unit
     * @return
     */
    Direction isFocusingTo(InputUnit unit);

    /**
     * Return �������� ȭ������(Zooming)�Ѵ�.
     * @param unit
     * @return
     */
    Direction isZoomingTo(InputUnit unit);

    /**
     * ���� �̺�Ʈ�� �߻��Ѵ�.
     * @param unit
     * @return
     */
    boolean isSelecting(InputUnit unit);

}
