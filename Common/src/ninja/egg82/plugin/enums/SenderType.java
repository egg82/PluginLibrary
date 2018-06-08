package ninja.egg82.plugin.enums;

public enum SenderType {
	SERVER((byte) 0x01),
	PROXY((byte) 0x02),
	UNKNOWN((byte) 0xFF);
	
	private byte type;
	SenderType(byte type) {
		this.type = type;
	}
	public byte getType() {
		return type;
	}
	public static SenderType fromType(byte type) {
		for (SenderType t : SenderType.values()) {
			if (t.getType() == type) {
				return t;
			}
		}
		return SenderType.UNKNOWN;
	}
}
