package com.speedracersedge.game

/**
 * Manages the local party system for Speed Racers Edge.
 *
 * Current version:
 * - Maximum 4 players
 * - Party leader
 * - Send invitations
 * - Accept invitations
 * - Decline invitations
 * - Remove party members
 * - Ready / not-ready status
 *
 * Online synchronization will be added later.
 */
class PartyManager {

    companion object {
        const val MAX_PARTY_SIZE = 4
    }

    data class Player(
        val id: String,
        val name: String,
        val isLeader: Boolean = false,
        var isReady: Boolean = false
    )

    data class PartyInvite(
        val inviteId: String,
        val fromPlayerId: String,
        val fromPlayerName: String,
        val toPlayerId: String,
        val toPlayerName: String
    )

    private val partyMembers = mutableListOf<Player>()

    private val pendingInvites = mutableListOf<PartyInvite>()

    private var inviteCounter = 1

    init {
        // The local player starts as party leader.
        partyMembers.add(
            Player(
                id = "player_1",
                name = "You",
                isLeader = true,
                isReady = false
            )
        )
    }

    // ============================================================
    // PARTY INFORMATION
    // ============================================================

    fun getPartyMembers(): List<Player> {
        return partyMembers.toList()
    }

    fun getPartySize(): Int {
        return partyMembers.size
    }

    fun getAvailableSlots(): Int {
        return MAX_PARTY_SIZE - partyMembers.size
    }

    fun isPartyFull(): Boolean {
        return partyMembers.size >= MAX_PARTY_SIZE
    }

    fun isPlayerInParty(playerId: String): Boolean {
        return partyMembers.any {
            it.id == playerId
        }
    }

    // ============================================================
    // INVITATIONS
    // ============================================================

    /**
     * Sends an invitation to another player.
     *
     * Returns:
     * - PartyInvite when successful
     * - null when the invitation cannot be sent
     */
    fun sendInvite(
        playerId: String,
        playerName: String
    ): PartyInvite? {

        // Cannot invite someone already in the party.
        if (isPlayerInParty(playerId)) {
            return null
        }

        // Cannot invite when the party is full.
        if (isPartyFull()) {
            return null
        }

        // Do not send duplicate pending invitations.
        val alreadyInvited =
            pendingInvites.any {
                it.toPlayerId == playerId
            }

        if (alreadyInvited) {
            return null
        }

        val invite = PartyInvite(
            inviteId = "invite_${inviteCounter++}",
            fromPlayerId = "player_1",
            fromPlayerName = "You",
            toPlayerId = playerId,
            toPlayerName = playerName
        )

        pendingInvites.add(invite)

        return invite
    }

    /**
     * Returns all invitations currently waiting for a response.
     */
    fun getPendingInvites(): List<PartyInvite> {
        return pendingInvites.toList()
    }

    /**
     * Finds a specific invitation.
     */
    fun getInvite(inviteId: String): PartyInvite? {
        return pendingInvites.find {
            it.inviteId == inviteId
        }
    }

    // ============================================================
    // ACCEPT INVITE
    // ============================================================

    /**
     * Accepts an invitation.
     *
     * The invited player joins the party.
     *
     * Returns true when the player successfully joins.
     */
    fun acceptInvite(
        inviteId: String
    ): Boolean {

        val invite =
            pendingInvites.find {
                it.inviteId == inviteId
            } ?: return false

        // Party may have become full while the invitation
        // was waiting.
        if (isPartyFull()) {
            pendingInvites.remove(invite)
            return false
        }

        // Prevent duplicate members.
        if (isPlayerInParty(invite.toPlayerId)) {
            pendingInvites.remove(invite)
            return false
        }

        val newPlayer = Player(
            id = invite.toPlayerId,
            name = invite.toPlayerName,
            isLeader = false,
            isReady = false
        )

        partyMembers.add(newPlayer)

        // Invitation has now been handled.
        pendingInvites.remove(invite)

        return true
    }

    // ============================================================
    // DECLINE INVITE
    // ============================================================

    /**
     * Declines an invitation.
     *
     * The player does not join the party.
     */
    fun declineInvite(
        inviteId: String
    ): Boolean {

        val invite =
            pendingInvites.find {
                it.inviteId == inviteId
            } ?: return false

        pendingInvites.remove(invite)

        return true
    }

    // ============================================================
    // CANCEL INVITE
    // ============================================================

    /**
     * Allows the sender to cancel an invitation
     * before it is accepted.
     */
    fun cancelInvite(
        inviteId: String
    ): Boolean {

        val invite =
            pendingInvites.find {
                it.inviteId == inviteId
            } ?: return false

        pendingInvites.remove(invite)

        return true
    }

    // ============================================================
    // REMOVE PLAYER
    // ============================================================

    /**
     * Removes a player from the party.
     *
     * The party leader cannot be removed.
     */
    fun removePlayer(
        playerId: String
    ): Boolean {

        val player =
            partyMembers.find {
                it.id == playerId
            } ?: return false

        if (player.isLeader) {
            return false
        }

        partyMembers.remove(player)

        return true
    }

    // ============================================================
    // READY SYSTEM
    // ============================================================

    /**
     * Changes a player's ready state.
     */
    fun setReady(
        playerId: String,
        ready: Boolean
    ): Boolean {

        val player =
            partyMembers.find {
                it.id == playerId
            } ?: return false

        player.isReady = ready

        return true
    }

    /**
     * Returns true when every party member is ready.
     */
    fun areAllPlayersReady(): Boolean {

        if (partyMembers.isEmpty()) {
            return false
        }

        return partyMembers.all {
            it.isReady
        }
    }

    /**
     * Returns the number of ready players.
     */
    fun getReadyPlayerCount(): Int {
        return partyMembers.count {
            it.isReady
        }
    }

    // ============================================================
    // MATCH START
    // ============================================================

    /**
     * Checks whether the leader can start a match.
     */
    fun canStartMatch(): Boolean {

        val leaderExists =
            partyMembers.any {
                it.isLeader
            }

        if (!leaderExists) {
            return false
        }

        return areAllPlayersReady()
    }

    // ============================================================
    // LEAVE PARTY
    // ============================================================

    /**
     * Removes everyone except the local player.
     *
     * Useful when leaving a party or returning to
     * the main lobby.
     */
    fun leaveParty() {

        val leader =
            partyMembers.find {
                it.isLeader
            }

        partyMembers.clear()

        if (leader != null) {
            leader.isReady = false
            partyMembers.add(leader)
        }

        pendingInvites.clear()
    }

    // ============================================================
    // RESET
    // ============================================================

    /**
     * Completely resets the local party.
     */
    fun reset() {

        partyMembers.clear()
        pendingInvites.clear()

        inviteCounter = 1

        partyMembers.add(
            Player(
                id = "player_1",
                name = "You",
                isLeader = true,
                isReady = false
            )
        )
    }
}