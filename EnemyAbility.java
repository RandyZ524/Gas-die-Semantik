public enum EnemyAbility {
	FIRST_RESPONSE     (100, 0),
	SPEED_BOOST        (30,  600),
	FIELD_MISDIRECTION (100, 0);
	
	public int chancePerTenSeconds, maxFrames;
	
	EnemyAbility(int chance, int max) {
		chancePerTenSeconds = chance;
		maxFrames = max;
	}
	
}