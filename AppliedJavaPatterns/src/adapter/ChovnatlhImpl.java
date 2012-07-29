package adapter;

/**
 * pong = name wa'DIch = first Qav = last patlh = rank (title) ghom = group (organization) tlhap =
 * take (get) cher = set up (set) chu' = new chovnatlh = specimen (contact)
 * 
 * @author Thomas Freese
 */
public class ChovnatlhImpl implements Chovnatlh
{
	/**
     * 
     */
	private String ghom;

	/**
     * 
     */
	private String patlh;

	/**
     * 
     */
	private String QavPong;

	/**
     * 
     */
	private String wa$DIchPong;

	/**
	 * Creates a new ChovnatlhImpl object.
	 */
	public ChovnatlhImpl()
	{
		super();
	}

	/**
	 * Creates a new ChovnatlhImpl object.
	 * 
	 * @param chu$wa$DIchPong String
	 * @param chu$QavPong String
	 * @param chu$patlh String
	 * @param chu$ghom String
	 */
	public ChovnatlhImpl(final String chu$wa$DIchPong, final String chu$QavPong,
			final String chu$patlh, final String chu$ghom)
	{
		super();

		this.wa$DIchPong = chu$wa$DIchPong;
		this.QavPong = chu$QavPong;
		this.patlh = chu$patlh;
		this.ghom = chu$ghom;
	}

	/**
	 * @see adapter.Chovnatlh#cherGhom(java.lang.String)
	 */
	@Override
	public void cherGhom(final String chu$ghom)
	{
		this.ghom = chu$ghom;
	}

	/**
	 * @see adapter.Chovnatlh#cherPatlh(java.lang.String)
	 */
	@Override
	public void cherPatlh(final String chu$patlh)
	{
		this.patlh = chu$patlh;
	}

	/**
	 * @see adapter.Chovnatlh#cherQavPong(java.lang.String)
	 */
	@Override
	public void cherQavPong(final String chu$QavPong)
	{
		this.QavPong = chu$QavPong;
	}

	/**
	 * @see adapter.Chovnatlh#cherWa$DIchPong(java.lang.String)
	 */
	@Override
	public void cherWa$DIchPong(final String chu$wa$DIchPong)
	{
		this.wa$DIchPong = chu$wa$DIchPong;
	}

	/**
	 * @see adapter.Chovnatlh#tlhapGhom()
	 */
	@Override
	public String tlhapGhom()
	{
		return this.ghom;
	}

	/**
	 * @see adapter.Chovnatlh#tlhapPatlh()
	 */
	@Override
	public String tlhapPatlh()
	{
		return this.patlh;
	}

	/**
	 * @see adapter.Chovnatlh#tlhapQavPong()
	 */
	@Override
	public String tlhapQavPong()
	{
		return this.QavPong;
	}

	/**
	 * @see adapter.Chovnatlh#tlhapWa$DIchPong()
	 */
	@Override
	public String tlhapWa$DIchPong()
	{
		return this.wa$DIchPong;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.wa$DIchPong + " " + this.QavPong + ": " + this.patlh + ", " + this.ghom;
	}
}
