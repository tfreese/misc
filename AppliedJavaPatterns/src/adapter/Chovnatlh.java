package adapter;

/**
 * @author Thomas Freese
 */
public interface Chovnatlh
{
	/**
	 * @param chu$ghom String
	 */
	public void cherGhom(String chu$ghom);

	/**
	 * @param chu$patlh String
	 */
	public void cherPatlh(String chu$patlh);

	/**
	 * @param chu$QavPong String
	 */
	public void cherQavPong(String chu$QavPong);

	/**
	 * @param chu$wa$DIchPong String
	 */
	public void cherWa$DIchPong(String chu$wa$DIchPong);

	/**
	 * @return String
	 */
	public String tlhapGhom();

	/**
	 * @return String
	 */
	public String tlhapPatlh();

	/**
	 * @return String
	 */
	public String tlhapQavPong();

	/**
	 * @return String
	 */
	public String tlhapWa$DIchPong();
}
