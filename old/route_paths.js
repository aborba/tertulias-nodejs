module.exports = [

	home: {
		tertulias: 'tertulias',
		users:     'users'
	},

	linkKeys: {
		self: '',
	},

	paths: {
		// Lists
		tertulias:    'tertulias',                             // My tertulias
		public:       'public',                                // Public tertulias
		public_bytag: 'public/{tag}',                          // Public tertulias on a tag
		members:      'tertulia/{tr}/members',                 // Tertulia's members
		locations:    'tertulia/{tr}/locations',               // Tertulia's locations
		events:       'tertulia/{tr}/events',                  // Tertulia's events
		event_items:  'tertulia/{tr}/event/{ev}/items',        // Tertulia's events
		contribution: 'tertulia/{tr}/event/{ev}/contribution', // Tertulia's events
		items:        'tertulia/{tr}/events',                  // Tertulia's items
		templates:    'tertulia/{tr}/templates',               // Tertulia's templates of items
		template:     'tertulia/{tr}/template/{tp}',           // Tertulia's template items
		messages:     'tertulia/{tr}/messages',		           // Tertulia's unread broadcast messages
		invitations:  'tertulia/{tr}/invitations',             // Tertulia's pending invitations
		// Items
		tertulia:     'tertulia/{tr}',
		location:     'tertulia/{tr}/location',
		ilocation:    'tertulia/{tr}/location/{lo}',
		schedule:     'tertulia/{tr}/schedule',

		tertulia_edit:            'tertulia/{tr}/edit',
		tertulia_owner:           'tertulia/{tr}/owner',
		tertulia_nextevent:       'tertulia/{tr}/nextevent',
	}
];